package ua.ihromant.analisys;

import ua.ihromant.data.GameResult;
import ua.ihromant.data.Ladder;
import ua.ihromant.data.StatisticsItem;
import ua.ihromant.data.Template;

import java.io.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class RatingCalculator implements Function<List<GameResult>, Ladder> {
    private static final Predicate<GameResult> CONFIRMED = res -> res.getConfirmer().getReportLink() != null;
    private static final Map<String, StatisticsItem> OLD_ITEMS = getOldItems();
    private static final Set<Integer> SEASON_THEMES = new HashSet<>(Arrays.asList(14259, 12859,
            12410, 10979, 12145, 11151, 11634, 11896, 11369));

    private static Predicate<GameResult> themeFilter(Set<Integer> filteredThemes) {
        return res -> {
            int counter = res.getTourneyLink().lastIndexOf('=');
            return !filteredThemes.contains(Integer.parseInt(res.getTourneyLink().substring(counter + 1)));
        };
    }

    private final String name;
    private final Predicate<GameResult> gameFilter;
    private final Map<String, StatisticsItem> initial;

    public static RatingCalculator overall() {
        return new RatingCalculator("Overall rating", r -> true, OLD_ITEMS);
    }

    public static RatingCalculator tourneys() {
        return new RatingCalculator("Tournament rating", themeFilter(SEASON_THEMES));
    }

    public static RatingCalculator last2Years() {
        LocalDate now = LocalDate.now();
        return new RatingCalculator("Last 2 years rating", themeFilter(SEASON_THEMES).and(r -> now.minusYears(2).isBefore(r.getDate())));
    }

    public static RatingCalculator inSeason(LocalDate at) {
        LocalDate from;
        LocalDate to;
        String name;
        if (at.getMonth().ordinal() >= Month.JUNE.ordinal() && at.getMonth().ordinal() <= Month.NOVEMBER.ordinal()) {
            from = at.withMonth(Month.JUNE.getValue()).with(TemporalAdjusters.firstDayOfMonth());
            to = at.withMonth(Month.NOVEMBER.getValue()).with(TemporalAdjusters.lastDayOfMonth());
            name = "Summer-Autumn-" + at.getYear();
        } else {
            if (at.getMonth() == Month.DECEMBER) {
                from = at.with(TemporalAdjusters.firstDayOfMonth());
                to = at.withYear(at.getYear() + 1).withMonth(Month.MAY.getValue()).with(TemporalAdjusters.lastDayOfMonth());
                name = "Winter-Spring-" + (at.getYear() + 1);
            } else {
                from = at.withYear(at.getYear() - 1).withMonth(Month.DECEMBER.getValue()).with(TemporalAdjusters.firstDayOfMonth());
                to = at.withMonth(Month.MAY.getValue()).with(TemporalAdjusters.lastDayOfMonth());
                name = "Winter-Spring-" + at.getYear();
            }
        }
        return new RatingCalculator(name, res -> !res.getDate().isBefore(from) && !res.getDate().isAfter(to));
    }

    public static RatingCalculator inTourneySeason() {
        LocalDate now = LocalDate.now();
        return new RatingCalculator("Last 6 months rating", themeFilter(SEASON_THEMES).and(r -> now.minusMonths(6).isBefore(r.getDate())));
    }

    public static RatingCalculator templateCalculator(Template template) {
        return new RatingCalculator(template.getTemplateName() + " template rating",
                r -> r.getTemplate() == template || r.getTemplate().getParent() == template);
    }

    private RatingCalculator(String name, Predicate<GameResult> gameFilter, Map<String, StatisticsItem> initial) {
        this.name = name;
        this.gameFilter = gameFilter;
        this.initial = initial;
    }

    private RatingCalculator(String name, Predicate<GameResult> gameFilter) {
        this.name = name;
        this.gameFilter = gameFilter;
        this.initial = Collections.emptyMap();
    }

    @Override
    public Ladder apply(List<GameResult> results) {
        Ladder ladder = new Ladder();
        ladder.setName(name);
        Map<String, StatisticsItem> statistics = results.stream()
                .filter(CONFIRMED.and(gameFilter))
                .flatMap(res -> Stream.of(res.getConfirmer().getName(), res.getReporter().getName()))
                .distinct().collect(Collectors.toMap(Function.identity(), name -> {
                    if (initial.containsKey(name)) {
                        return initial.get(name);
                    }
                    StatisticsItem it = new StatisticsItem();
                    it.setName(name);
                    it.setRating(1500);
                    return it;
                }));
        ladder.setActivities(results.stream()
                .filter(CONFIRMED.and(gameFilter))
                .collect(Collectors.groupingBy(res -> res.getDate().with(TemporalAdjusters.firstDayOfMonth()),
                        TreeMap::new,
                        Collectors.counting())));
        results.stream()
                .filter(CONFIRMED.and(gameFilter))
                .sorted(Comparator.comparing(GameResult::getDate))
                .forEach(res -> {
                    StatisticsItem reporterStats = statistics.get(res.getReporter().getName());
                    StatisticsItem confirmerStats = statistics.get(res.getConfirmer().getName());
                    switch (res.getResult()) {
                        case DEF:
                            recalculateWin(reporterStats, confirmerStats, res.cloned());
                            break;
                        case LOSE:
                            recalculateWin(confirmerStats, reporterStats, res.reversed());
                            break;
                        default:
                            recalculateDraw(reporterStats, confirmerStats, res.cloned());
                            break;
                    }
                    ladder.getTimingMap().compute(timingKey(res.getTiming()), (key, old) -> old != null ? old + 1 : 1);
                    ladder.setTotalGames(ladder.getTotalGames() + 1);
                });
        List<StatisticsItem> ordered = statistics.entrySet().stream()
                .map(Map.Entry::getValue)
                .sorted(Comparator.comparing(StatisticsItem::getRating).reversed())
                .collect(Collectors.toList());
        ladder.setItems(IntStream.range(0, ordered.size())
                .mapToObj(i -> {
                    StatisticsItem item = ordered.get(i);
                    item.setRank(i + 1);
                    return item;
                })
                .collect(Collectors.toMap(it -> it.getName().toLowerCase(),
                        Function.identity(),
                        (u,v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); },
                        LinkedHashMap::new)));
        return ladder;
    }

    private static String timingKey(String timing) {
        if ("114".compareTo(timing) >= 0) {
            return "114-";
        }
        if ("221".compareTo(timing) <= 0 && "227".compareTo(timing) >= 0) {
            return "221-227";
        }
        if ("231".compareTo(timing) <= 0) {
            return "231+";
        }
        return timing;
    }

    private void recalculateWin(StatisticsItem winner, StatisticsItem loser, GameResult result) {
        float f1 = Math.max(-1, Math.min(1.0f * (winner.getRating() - loser.getRating()) / 400, 1));
        float f2 = Math.max(-1, Math.min(1.0f * (loser.getRating() - winner.getRating()) / 400, 1));

        int wp2 = Math.max(1, Math.round(16 * (1 + f2)));
        int lp1 = Math.min(-1, Math.round(16 * (-1 + f1)));

        result.setPreviousReporter(winner.getRating());
        result.setPreviousConfirmer(loser.getRating());
        result.setReporterChange(wp2);
        result.setConfirmerChange(lp1);

        winner.setRating(winner.getRating() + wp2);
        winner.setWins(winner.getWins() + 1);
        winner.getResults().add(0, result);

        loser.setRating(loser.getRating() + lp1);
        loser.setLoses(loser.getLoses() + 1);
        loser.getResults().add(0, result.reversed());
    }

    private void recalculateDraw(StatisticsItem first, StatisticsItem second, GameResult result) {
        float f1 = Math.max(-1, Math.min(1.0f * (first.getRating() - second.getRating()) / 400, 1));
        float f2 = Math.max(-1, Math.min(1.0f * (second.getRating() - first.getRating()) / 400, 1));

        float wp1 = Math.max(1, 16 * (1 + f1));
        float wp2 = Math.max(1, 16 * (1 + f2));

        result.setReporterChange(Math.round(((wp2 - wp1) / 2 + 0.5f)));
        result.setConfirmerChange(Math.round(((wp1 - wp2) / 2 + 0.5f)));
        result.setPreviousReporter(first.getRating());
        result.setPreviousConfirmer(second.getRating());

        first.setRating(first.getRating() + result.getReporterChange());
        first.setDraws(first.getDraws() + 1);
        first.getResults().add(0, result);

        second.setRating(second.getRating() + result.getConfirmerChange());
        second.setDraws(second.getDraws() + 1);
        second.getResults().add(0, result.reversed());
    }

    private static Map<String, StatisticsItem> getOldItems() {
        try (InputStream is = RatingCalculator.class.getResourceAsStream("/oldRating.csv");
             InputStreamReader isr = new InputStreamReader(is);
             BufferedReader br = new BufferedReader(isr)) {
            return br.lines().map(line -> {
                String[] split = line.split(",");
                StatisticsItem item = new StatisticsItem();
                item.setName(split[1]);
                int wins = Integer.parseInt(split[2]);
                item.setWins(wins);
                int loses = Integer.parseInt(split[3]);
                item.setLoses(loses);
                item.setDraws(Integer.parseInt(split[4]) - wins - loses);
                item.setRating(Integer.parseInt(split[5]));
                return item;
            }).collect(Collectors.toMap(StatisticsItem::getName, Function.identity()));
        } catch (IOException e) {
            throw new RuntimeException("Was not able to read config file", e);
        }
    }
}
