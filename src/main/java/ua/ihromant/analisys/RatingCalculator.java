package ua.ihromant.analisys;

import ua.ihromant.analisys.builder.*;
import ua.ihromant.data.GameResult;
import ua.ihromant.data.Ladder;
import ua.ihromant.data.PlayerStatisticsItem;
import ua.ihromant.data.Template;

import java.io.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RatingCalculator implements Function<List<GameResult>, Ladder> {
    private static final Predicate<GameResult> CONFIRMED = res -> res.getConfirmer().getReportLink() != null;
    private static final Map<String, PlayerStatisticsItem> OLD_ITEMS = getOldItems();
    private static final Set<Integer> SEASON_THEMES = new HashSet<>(Arrays.asList(14259, 12859,
            12410, 10979, 12145, 11151, 11634, 11896, 11369, 14413, 10864, 10746));

    private static Predicate<GameResult> themeFilter(Set<Integer> filteredThemes) {
        return res -> {
            int counter = res.getTourneyLink().lastIndexOf('=');
            return !filteredThemes.contains(Integer.parseInt(res.getTourneyLink().substring(counter + 1)));
        };
    }

    private final String name;
    private final Predicate<GameResult> gameFilter;
    private final List<Supplier<StatisticsBuilder>> builders;

    public static RatingCalculator overall(LocalDate now) {
        return new RatingCalculator("Overall rating", r -> true, now, OLD_ITEMS);
    }

    public static RatingCalculator tourneys(LocalDate now) {
        return new RatingCalculator("Tournament rating", themeFilter(SEASON_THEMES), now);
    }

    public static RatingCalculator last2Years(LocalDate now) {
        LocalDate date = now.minusYears(2);
        return new RatingCalculator("Last 2 years rating", themeFilter(SEASON_THEMES).and(r -> date.isBefore(r.getDate())), now);
    }

    public static RatingCalculator inSeason(LocalDate now, int minusMonths) {
        LocalDate at = now.minusMonths(minusMonths);
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
        return new RatingCalculator(name, res -> !res.getDate().isBefore(from) && !res.getDate().isAfter(to), now);
    }

    public static RatingCalculator inTourneySeason(LocalDate now) {
        LocalDate date = now.minusMonths(6);
        return new RatingCalculator("Last 6 months rating", themeFilter(SEASON_THEMES).and(r -> date.isBefore(r.getDate())), now);
    }

    public static RatingCalculator templateCalculator(Template template, LocalDate now) {
        return new RatingCalculator(template.getTemplateName() + " template rating",
                r -> r.getTemplate() == template || r.getTemplate().getParent() == template, now);
    }

    private RatingCalculator(String name, Predicate<GameResult> gameFilter, LocalDate now, Map<String, PlayerStatisticsItem> initial) {
        this.name = name;
        this.gameFilter = gameFilter;
        this.builders = Arrays.asList(TimingBuilder::new,
                TotalGamesBuilder::new,
                () -> new ActivityBuilder(now.with(TemporalAdjusters.firstDayOfMonth())),
                () -> new StatisticsItemsBuilder(initial),
                () -> new PlayerActivityBuilder(now.with(TemporalAdjusters.firstDayOfMonth())));
    }

    private RatingCalculator(String name, Predicate<GameResult> gameFilter, LocalDate now) {
        this(name, gameFilter, now, Collections.emptyMap());
    }

    @Override
    public Ladder apply(List<GameResult> results) {
        Ladder ladder = new Ladder();
        ladder.setName(name);
        List<GameResult> prepared = results.stream()
                .filter(CONFIRMED.and(gameFilter))
                .sorted(Comparator.comparing(GameResult::getDate))
                .collect(Collectors.toList());
        List<StatisticsBuilder> builders = this.builders.stream().map(Supplier::get).collect(Collectors.toList());
        builders.forEach(builder -> builder.init(prepared));
        prepared.forEach(result -> builders.forEach(builder -> builder.append(result)));
        builders.forEach(builder -> builder.set(ladder));
        return ladder;
    }

    private static Map<String, PlayerStatisticsItem> getOldItems() {
        try (InputStream is = RatingCalculator.class.getResourceAsStream("/oldRating.csv");
             InputStreamReader isr = new InputStreamReader(is);
             BufferedReader br = new BufferedReader(isr)) {
            return br.lines().map(line -> {
                String[] split = line.split(",");
                PlayerStatisticsItem item = new PlayerStatisticsItem();
                item.setName(split[1]);
                int wins = Integer.parseInt(split[2]);
                item.setWins(wins);
                int loses = Integer.parseInt(split[3]);
                item.setLoses(loses);
                item.setDraws(Integer.parseInt(split[4]) - wins - loses);
                item.setRating(Integer.parseInt(split[5]));
                return item;
            }).collect(Collectors.toMap(PlayerStatisticsItem::getName, Function.identity()));
        } catch (IOException e) {
            throw new RuntimeException("Was not able to read config file", e);
        }
    }
}
