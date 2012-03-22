/*
 * Copyright (C) 2008-11  Bernhard Hobiger
 *
 * This file is part of HoDoKu.
 *
 * HoDoKu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * HoDoKu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with HoDoKu. If not, see <http://www.gnu.org/licenses/>.
 */
package sudoku;

import generator.BackgroundGeneratorThread;
import generator.GeneratorPattern;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hobiwan
 */
public final class Options {

    public static final String FILE_NAME = "hodoku.hcfg";
    private static final ProgressComparator progressComparator = new ProgressComparator();
    // Schwierigkeitsstufen
    public static final DifficultyLevel[] DEFAULT_DIFFICULTY_LEVELS = {
        new DifficultyLevel(DifficultyType.INCOMPLETE, 0, java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.incomplete"), Color.BLACK, Color.WHITE),
        new DifficultyLevel(DifficultyType.EASY, 600, java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.easy"), Color.WHITE, Color.BLACK),
        new DifficultyLevel(DifficultyType.MEDIUM, 1000, java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.medium"), new Color(100, 255, 100), Color.BLACK),
        new DifficultyLevel(DifficultyType.HARD, 3500, java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.hard"), new Color(255, 255, 100), Color.BLACK),
        new DifficultyLevel(DifficultyType.UNFAIR, 5000, java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.unfair"), new Color(255, 150, 80), Color.BLACK),
        new DifficultyLevel(DifficultyType.EXTREME, Integer.MAX_VALUE, java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.extreme"), new Color(255, 100, 100), Color.BLACK)
    };
    private DifficultyLevel[] difficultyLevels = null;
    // Reihenfolge und Konfiguration der SolutionSteps
    // ACHTUNG: New solver steps must be added at the end of the array! The position is determined by "index"
    public static final StepConfig[] DEFAULT_SOLVER_STEPS = {
        new StepConfig(Integer.MAX_VALUE - 1, SolutionType.INCOMPLETE, DifficultyType.INCOMPLETE.ordinal(), SolutionCategory.LAST_RESORT, 0, 0, false, false, Integer.MAX_VALUE - 1, false, false),
        new StepConfig(Integer.MAX_VALUE, SolutionType.GIVE_UP, DifficultyType.EXTREME.ordinal(), SolutionCategory.LAST_RESORT, 20000, 0, true, false, Integer.MAX_VALUE, true, false),
        new StepConfig(100, SolutionType.FULL_HOUSE, DifficultyType.EASY.ordinal(), SolutionCategory.SINGLES, 4, 0, true, true, 100, true, false),
        new StepConfig(200, SolutionType.NAKED_SINGLE, DifficultyType.EASY.ordinal(), SolutionCategory.SINGLES, 4, 0, true, true, 200, true, false),
        new StepConfig(300, SolutionType.HIDDEN_SINGLE, DifficultyType.EASY.ordinal(), SolutionCategory.SINGLES, 14, 0, true, true, 300, true, false),
        new StepConfig(1000, SolutionType.LOCKED_PAIR, DifficultyType.MEDIUM.ordinal(), SolutionCategory.INTERSECTIONS, 40, 0, true, true, 1000, true, false),
        new StepConfig(1100, SolutionType.LOCKED_TRIPLE, DifficultyType.MEDIUM.ordinal(), SolutionCategory.INTERSECTIONS, 60, 0, true, true, 1100, true, false),
        new StepConfig(1200, SolutionType.LOCKED_CANDIDATES, DifficultyType.MEDIUM.ordinal(), SolutionCategory.INTERSECTIONS, 50, 0, true, true, 1200, true, false),
        new StepConfig(1300, SolutionType.NAKED_PAIR, DifficultyType.MEDIUM.ordinal(), SolutionCategory.SUBSETS, 60, 0, true, true, 1300, true, false),
        new StepConfig(1400, SolutionType.NAKED_TRIPLE, DifficultyType.MEDIUM.ordinal(), SolutionCategory.SUBSETS, 80, 0, true, true, 1400, true, false),
        new StepConfig(1500, SolutionType.HIDDEN_PAIR, DifficultyType.MEDIUM.ordinal(), SolutionCategory.SUBSETS, 70, 0, true, true, 1500, true, false),
        new StepConfig(1600, SolutionType.HIDDEN_TRIPLE, DifficultyType.MEDIUM.ordinal(), SolutionCategory.SUBSETS, 100, 0, true, true, 1600, true, false),
        new StepConfig(2000, SolutionType.NAKED_QUADRUPLE, DifficultyType.HARD.ordinal(), SolutionCategory.SUBSETS, 120, 0, true, true, 2000, true, false),
        new StepConfig(2100, SolutionType.HIDDEN_QUADRUPLE, DifficultyType.HARD.ordinal(), SolutionCategory.SUBSETS, 150, 0, true, true, 2100, true, false),
        new StepConfig(2200, SolutionType.X_WING, DifficultyType.HARD.ordinal(), SolutionCategory.BASIC_FISH, 140, 0, true, false, 2200, false, false),
        new StepConfig(2300, SolutionType.SWORDFISH, DifficultyType.HARD.ordinal(), SolutionCategory.BASIC_FISH, 150, 0, true, false, 2300, false, false),
        new StepConfig(2400, SolutionType.JELLYFISH, DifficultyType.HARD.ordinal(), SolutionCategory.BASIC_FISH, 160, 0, true, false, 2400, false, false),
        new StepConfig(2500, SolutionType.SQUIRMBAG, DifficultyType.UNFAIR.ordinal(), SolutionCategory.BASIC_FISH, 470, 0, false, false, 2500, false, false),
        new StepConfig(2600, SolutionType.WHALE, DifficultyType.UNFAIR.ordinal(), SolutionCategory.BASIC_FISH, 470, 0, false, false, 2600, false, false),
        new StepConfig(2700, SolutionType.LEVIATHAN, DifficultyType.UNFAIR.ordinal(), SolutionCategory.BASIC_FISH, 470, 0, false, false, 2700, false, false),
        new StepConfig(2800, SolutionType.REMOTE_PAIR, DifficultyType.HARD.ordinal(), SolutionCategory.CHAINS_AND_LOOPS, 110, 0, true, true, 2800, false, false),
        new StepConfig(2900, SolutionType.BUG_PLUS_1, DifficultyType.HARD.ordinal(), SolutionCategory.UNIQUENESS, 100, 0, true, true, 2900, false, false),
        new StepConfig(3000, SolutionType.SKYSCRAPER, DifficultyType.HARD.ordinal(), SolutionCategory.SINGLE_DIGIT_PATTERNS, 130, 0, true, true, 3000, false, false),
        new StepConfig(3200, SolutionType.W_WING, DifficultyType.HARD.ordinal(), SolutionCategory.WINGS, 150, 0, true, true, 3200, false, false),
        new StepConfig(3100, SolutionType.TWO_STRING_KITE, DifficultyType.HARD.ordinal(), SolutionCategory.SINGLE_DIGIT_PATTERNS, 150, 0, true, true, 3100, false, false),
        new StepConfig(3300, SolutionType.XY_WING, DifficultyType.HARD.ordinal(), SolutionCategory.WINGS, 160, 0, true, true, 3300, false, false),
        new StepConfig(3400, SolutionType.XYZ_WING, DifficultyType.HARD.ordinal(), SolutionCategory.WINGS, 180, 0, true, true, 3400, false, false),
        new StepConfig(3500, SolutionType.UNIQUENESS_1, DifficultyType.HARD.ordinal(), SolutionCategory.UNIQUENESS, 100, 0, true, true, 3500, false, false),
        new StepConfig(3600, SolutionType.UNIQUENESS_2, DifficultyType.HARD.ordinal(), SolutionCategory.UNIQUENESS, 100, 0, true, true, 3600, false, false),
        new StepConfig(3700, SolutionType.UNIQUENESS_3, DifficultyType.HARD.ordinal(), SolutionCategory.UNIQUENESS, 100, 0, true, true, 3700, false, false),
        new StepConfig(3800, SolutionType.UNIQUENESS_4, DifficultyType.HARD.ordinal(), SolutionCategory.UNIQUENESS, 100, 0, true, true, 3800, false, false),
        new StepConfig(3900, SolutionType.UNIQUENESS_5, DifficultyType.HARD.ordinal(), SolutionCategory.UNIQUENESS, 100, 0, true, true, 3900, false, false),
        new StepConfig(4000, SolutionType.UNIQUENESS_6, DifficultyType.HARD.ordinal(), SolutionCategory.UNIQUENESS, 100, 0, true, true, 4000, false, false),
        new StepConfig(4100, SolutionType.FINNED_X_WING, DifficultyType.HARD.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 130, 0, true, false, 4100, false, false),
        new StepConfig(4200, SolutionType.SASHIMI_X_WING, DifficultyType.HARD.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 150, 0, true, false, 4200, false, false),
        new StepConfig(4300, SolutionType.FINNED_SWORDFISH, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 200, 0, true, false, 4300, false, false),
        new StepConfig(4400, SolutionType.SASHIMI_SWORDFISH, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 240, 0, true, false, 4400, false, false),
        new StepConfig(4500, SolutionType.FINNED_JELLYFISH, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 250, 0, true, false, 4500, false, false),
        new StepConfig(4600, SolutionType.SASHIMI_JELLYFISH, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 260, 0, true, false, 4600, false, false),
        new StepConfig(4700, SolutionType.FINNED_SQUIRMBAG, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 470, 0, false, false, 4700, false, false),
        new StepConfig(4800, SolutionType.SASHIMI_SQUIRMBAG, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 470, 0, false, false, 4800, false, false),
        new StepConfig(4900, SolutionType.FINNED_WHALE, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 470, 0, false, false, 4900, false, false),
        new StepConfig(5000, SolutionType.SASHIMI_WHALE, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 470, 0, false, false, 5000, false, false),
        new StepConfig(5100, SolutionType.FINNED_LEVIATHAN, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 470, 0, false, false, 5100, false, false),
        new StepConfig(5200, SolutionType.SASHIMI_LEVIATHAN, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FINNED_BASIC_FISH, 470, 0, false, false, 5200, false, false),
        new StepConfig(5300, SolutionType.SUE_DE_COQ, DifficultyType.UNFAIR.ordinal(), SolutionCategory.MISCELLANEOUS, 250, 0, true, true, 5300, false, false),
        new StepConfig(5400, SolutionType.X_CHAIN, DifficultyType.UNFAIR.ordinal(), SolutionCategory.CHAINS_AND_LOOPS, 260, 0, true, true, 5400, false, false),
        new StepConfig(5500, SolutionType.XY_CHAIN, DifficultyType.UNFAIR.ordinal(), SolutionCategory.CHAINS_AND_LOOPS, 260, 0, true, true, 5500, false, false),
        new StepConfig(5600, SolutionType.NICE_LOOP, DifficultyType.UNFAIR.ordinal(), SolutionCategory.CHAINS_AND_LOOPS, 280, 0, true, true, 5600, false, false),
        new StepConfig(5700, SolutionType.ALS_XZ, DifficultyType.UNFAIR.ordinal(), SolutionCategory.ALMOST_LOCKED_SETS, 300, 0, true, true, 5700, false, false),
        new StepConfig(5800, SolutionType.ALS_XY_WING, DifficultyType.UNFAIR.ordinal(), SolutionCategory.ALMOST_LOCKED_SETS, 320, 0, true, true, 5800, false, false),
        new StepConfig(5900, SolutionType.ALS_XY_CHAIN, DifficultyType.UNFAIR.ordinal(), SolutionCategory.ALMOST_LOCKED_SETS, 340, 0, true, true, 5900, false, false),
        new StepConfig(6000, SolutionType.DEATH_BLOSSOM, DifficultyType.UNFAIR.ordinal(), SolutionCategory.ALMOST_LOCKED_SETS, 360, 0, false, true, 6000, false, false),
        new StepConfig(6100, SolutionType.FRANKEN_X_WING, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FRANKEN_FISH, 300, 0, true, false, 6100, false, false),
        new StepConfig(6200, SolutionType.FRANKEN_SWORDFISH, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FRANKEN_FISH, 350, 0, true, false, 6200, false, false),
        new StepConfig(6300, SolutionType.FRANKEN_JELLYFISH, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FRANKEN_FISH, 370, 0, false, false, 6300, false, false),
        new StepConfig(6400, SolutionType.FRANKEN_SQUIRMBAG, DifficultyType.EXTREME.ordinal(), SolutionCategory.FRANKEN_FISH, 470, 0, false, false, 6400, false, false),
        new StepConfig(6500, SolutionType.FRANKEN_WHALE, DifficultyType.EXTREME.ordinal(), SolutionCategory.FRANKEN_FISH, 470, 0, false, false, 6500, false, false),
        new StepConfig(6600, SolutionType.FRANKEN_LEVIATHAN, DifficultyType.EXTREME.ordinal(), SolutionCategory.FRANKEN_FISH, 470, 0, false, false, 6600, false, false),
        new StepConfig(6700, SolutionType.FINNED_FRANKEN_X_WING, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FINNED_FRANKEN_FISH, 390, 0, true, false, 6700, false, false),
        new StepConfig(6800, SolutionType.FINNED_FRANKEN_SWORDFISH, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FINNED_FRANKEN_FISH, 410, 0, true, false, 6800, false, false),
        new StepConfig(6900, SolutionType.FINNED_FRANKEN_JELLYFISH, DifficultyType.UNFAIR.ordinal(), SolutionCategory.FINNED_FRANKEN_FISH, 430, 0, false, false, 6900, false, false),
        new StepConfig(7000, SolutionType.FINNED_FRANKEN_SQUIRMBAG, DifficultyType.EXTREME.ordinal(), SolutionCategory.FINNED_FRANKEN_FISH, 470, 0, false, false, 7000, false, false),
        new StepConfig(7100, SolutionType.FINNED_FRANKEN_WHALE, DifficultyType.EXTREME.ordinal(), SolutionCategory.FINNED_FRANKEN_FISH, 470, 0, false, false, 7100, false, false),
        new StepConfig(7200, SolutionType.FINNED_FRANKEN_LEVIATHAN, DifficultyType.EXTREME.ordinal(), SolutionCategory.FINNED_FRANKEN_FISH, 470, 0, false, false, 7200, false, false),
        new StepConfig(7300, SolutionType.MUTANT_X_WING, DifficultyType.EXTREME.ordinal(), SolutionCategory.MUTANT_FISH, 450, 0, false, false, 7300, false, false),
        new StepConfig(7400, SolutionType.MUTANT_SWORDFISH, DifficultyType.EXTREME.ordinal(), SolutionCategory.MUTANT_FISH, 450, 0, false, false, 7400, false, false),
        new StepConfig(7500, SolutionType.MUTANT_JELLYFISH, DifficultyType.EXTREME.ordinal(), SolutionCategory.MUTANT_FISH, 450, 0, false, false, 7500, false, false),
        new StepConfig(7600, SolutionType.MUTANT_SQUIRMBAG, DifficultyType.EXTREME.ordinal(), SolutionCategory.MUTANT_FISH, 470, 0, false, false, 7600, false, false),
        new StepConfig(7700, SolutionType.MUTANT_WHALE, DifficultyType.EXTREME.ordinal(), SolutionCategory.MUTANT_FISH, 470, 0, false, false, 7700, false, false),
        new StepConfig(7800, SolutionType.MUTANT_LEVIATHAN, DifficultyType.EXTREME.ordinal(), SolutionCategory.MUTANT_FISH, 470, 0, false, false, 7800, false, false),
        new StepConfig(7900, SolutionType.FINNED_MUTANT_X_WING, DifficultyType.EXTREME.ordinal(), SolutionCategory.FINNED_MUTANT_FISH, 470, 0, false, false, 7900, false, false),
        new StepConfig(8000, SolutionType.FINNED_MUTANT_SWORDFISH, DifficultyType.EXTREME.ordinal(), SolutionCategory.FINNED_MUTANT_FISH, 470, 0, false, false, 8000, false, false),
        new StepConfig(8100, SolutionType.FINNED_MUTANT_JELLYFISH, DifficultyType.EXTREME.ordinal(), SolutionCategory.FINNED_MUTANT_FISH, 470, 0, false, false, 8100, false, false),
        new StepConfig(8200, SolutionType.FINNED_MUTANT_SQUIRMBAG, DifficultyType.EXTREME.ordinal(), SolutionCategory.FINNED_MUTANT_FISH, 470, 0, false, false, 8200, false, false),
        new StepConfig(8300, SolutionType.FINNED_MUTANT_WHALE, DifficultyType.EXTREME.ordinal(), SolutionCategory.FINNED_MUTANT_FISH, 470, 0, false, false, 8300, false, false),
        new StepConfig(8400, SolutionType.FINNED_MUTANT_LEVIATHAN, DifficultyType.EXTREME.ordinal(), SolutionCategory.FINNED_MUTANT_FISH, 470, 0, false, false, 8400, false, false),
        new StepConfig(8700, SolutionType.TEMPLATE_SET, DifficultyType.EXTREME.ordinal(), SolutionCategory.LAST_RESORT, 10000, 0, false, false, 8700, false, false),
        new StepConfig(8800, SolutionType.TEMPLATE_DEL, DifficultyType.EXTREME.ordinal(), SolutionCategory.LAST_RESORT, 10000, 0, false, false, 8800, false, false),
        new StepConfig(8500, SolutionType.FORCING_CHAIN, DifficultyType.EXTREME.ordinal(), SolutionCategory.LAST_RESORT, 500, 0, true, false, 8500, false, false),
        new StepConfig(8600, SolutionType.FORCING_NET, DifficultyType.EXTREME.ordinal(), SolutionCategory.LAST_RESORT, 700, 0, true, false, 8600, false, false),
        new StepConfig(8900, SolutionType.BRUTE_FORCE, DifficultyType.EXTREME.ordinal(), SolutionCategory.LAST_RESORT, 10000, 0, true, false, 8900, false, false),
        new StepConfig(5650, SolutionType.GROUPED_NICE_LOOP, DifficultyType.UNFAIR.ordinal(), SolutionCategory.CHAINS_AND_LOOPS, 300, 0, true, true, 5650, false, false),
        new StepConfig(3170, SolutionType.EMPTY_RECTANGLE, DifficultyType.HARD.ordinal(), SolutionCategory.SINGLE_DIGIT_PATTERNS, 120, 0, true, true, 3170, false, false),
        new StepConfig(4010, SolutionType.HIDDEN_RECTANGLE, DifficultyType.HARD.ordinal(), SolutionCategory.UNIQUENESS, 100, 0, true, true, 4010, false, false),
        new StepConfig(4020, SolutionType.AVOIDABLE_RECTANGLE_1, DifficultyType.HARD.ordinal(), SolutionCategory.UNIQUENESS, 100, 0, true, true, 4020, false, false),
        new StepConfig(4030, SolutionType.AVOIDABLE_RECTANGLE_2, DifficultyType.HARD.ordinal(), SolutionCategory.UNIQUENESS, 100, 0, true, true, 4030, false, false),
        new StepConfig(5330, SolutionType.SIMPLE_COLORS, DifficultyType.HARD.ordinal(), SolutionCategory.COLORING, 150, 0, true, true, 5330, false, false),
        new StepConfig(5360, SolutionType.MULTI_COLORS, DifficultyType.HARD.ordinal(), SolutionCategory.COLORING, 200, 0, true, true, 5360, false, false),
        new StepConfig(8450, SolutionType.KRAKEN_FISH, DifficultyType.EXTREME.ordinal(), SolutionCategory.LAST_RESORT, 500, 0, false, false, 8450, false, false),
        new StepConfig(3120, SolutionType.TURBOT_FISH, DifficultyType.HARD.ordinal(), SolutionCategory.SINGLE_DIGIT_PATTERNS, 120, 0, true, true, 3120, false, false)
    };
    // nicht sortierte steps mit allen �nderungen -> wird so in *.cfg-File geschrieben
    private StepConfig[] orgSolverSteps = null;
    // sortierte Kopie, wird intern verwendet, darf aber nicht im *.cfg-File landen
    public StepConfig[] solverSteps = null;
    // sortierte Kopie f�r Step-Progress, wird intern verwendet, darf aber nicht im *.cfg-File landen
    public StepConfig[] solverStepsProgress = null;
    // internal cache for background creation
    public static final int CACHE_SIZE = 10;
    private String[][] normalPuzzles = new String[5][CACHE_SIZE]; // 10 puzzles per DifficultyLevel
    private String[] learningPuzzles = new String[CACHE_SIZE];    // 10 puzzles for training
    private String[] practisingPuzzles = new String[CACHE_SIZE];  // 10 puzzles for practising
    private int practisingPuzzlesLevel = -1;                      // the DifficultyLevel, for which the practising puzzles have been created
    // ChainSolver
    public static final int RESTRICT_CHAIN_LENGTH = 20;      // maximale L�nge von X-/XY-Chains, wenn restrictChainSize gesetzt ist
    public static final int RESTRICT_NICE_LOOP_LENGTH = 10;  // maximale L�nge von Nice-Loops, wenn restrictChainSize gesetzt ist
    public static final boolean RESTRICT_CHAIN_SIZE = true;  // L�nge der chains beschr�nken?
    private int restrictChainLength = RESTRICT_CHAIN_LENGTH;
    private int restrictNiceLoopLength = RESTRICT_NICE_LOOP_LENGTH;
    private boolean restrictChainSize = RESTRICT_CHAIN_SIZE;
    // TablingSolver
    public static final int MAX_TABLE_ENTRY_LENGTH = 1000;
//    public static final int MAX_TABLE_ENTRY_LENGTH = 400;
    public static final int ANZ_TABLE_LOOK_AHEAD = 4;
    public static final boolean ONLY_ONE_CHAIN_PER_STEP = true;
    public static final boolean ALLOW_ALS_IN_TABLING_CHAINS = false;
    public static final boolean ALL_STEPS_ALLOW_ALS_IN_TABLING_CHAINS = true;
    private int maxTableEntryLength = MAX_TABLE_ENTRY_LENGTH;
    private int anzTableLookAhead = ANZ_TABLE_LOOK_AHEAD;
    private boolean onlyOneChainPerStep = ONLY_ONE_CHAIN_PER_STEP;
    private boolean allowAlsInTablingChains = ALLOW_ALS_IN_TABLING_CHAINS;
    private boolean allStepsAllowAlsInTablingChains = ALL_STEPS_ALLOW_ALS_IN_TABLING_CHAINS;
    // AlsSolver
    public static final boolean ONLY_ONE_ALS_PER_STEP = true; // only one step in every ALS elimination
    public static final boolean ALLOW_ALS_OVERLAP = false;    // allow ALS steps with overlap (runtime!)
    public static final boolean ALL_STEPS_ONLY_ONE_ALS_PER_STEP = true; // only one step in every ALS elimination
    public static final boolean ALL_STEPS_ALLOW_ALS_OVERLAP = true;    // allow ALS steps with overlap (runtime!)
    private boolean onlyOneAlsPerStep = ONLY_ONE_ALS_PER_STEP;
    private boolean allowAlsOverlap = ALLOW_ALS_OVERLAP;
    private boolean allStepsOnlyOneAlsPerStep = ALL_STEPS_ONLY_ONE_ALS_PER_STEP;
    private boolean allStepsAllowAlsOverlap = ALL_STEPS_ALLOW_ALS_OVERLAP;
    // FishSolver
    public static final int MAX_FINS = 5;                 // Maximale Anzahl Fins
    public static final int MAX_ENDO_FINS = 2;            // Maximale Anzahl Endo-Fins
    public static final boolean CHECK_TEMPLATES = true;   // Template-Check um Kandidaten von der Suche auszuschlie�en
    public static final int KRAKEN_MAX_FISH_TYPE = 1;     // 0: nur basic, 1: basic+franken, 2: basic+franken+mutant
    public static final int KRAKEN_MAX_FISH_SIZE = 4;     // number of units in base/cover sets
    public static final int MAX_KRAKEN_FINS = 2;          // Maximale Anzahl Fins f�r Kraken-Suche
    public static final int MAX_KRAKEN_ENDO_FINS = 0;     // Maximale Anzahl Endo-Fins f�r Kraken-Suche
    public static final boolean ONLY_ONE_FISH_PER_STEP = true; // only the smallest fish for every elimination
    public static final int FISH_DISPLAY_MODE = 0;        // 0: normal; 1: statistics numbers; 2: statistics cells
    private int maxFins = MAX_FINS;
    private int maxEndoFins = MAX_ENDO_FINS;
    private boolean checkTemplates = CHECK_TEMPLATES;
    private int krakenMaxFishType = KRAKEN_MAX_FISH_TYPE;
    private int krakenMaxFishSize = KRAKEN_MAX_FISH_SIZE;
    private int maxKrakenFins = MAX_KRAKEN_FINS;
    private int maxKrakenEndoFins = MAX_KRAKEN_ENDO_FINS;
    private boolean onlyOneFishPerStep = ONLY_ONE_FISH_PER_STEP;
    private int fishDisplayMode = FISH_DISPLAY_MODE;
    // Search all steps
    public static final boolean ALL_STEPS_SEARCH_FISH = true; // search for Fish in "All Steps" panel
    public static final int ALL_STEPS_MAX_FISH_TYPE = 1;     // 0: nur basic, 1: basic+franken, 2: basic+franken+mutant
    public static final int ALL_STEPS_MIN_FISH_SIZE = 2;     // number of units in base/cover sets
    public static final int ALL_STEPS_MAX_FISH_SIZE = 4;     // number of units in base/cover sets
    public static final int ALL_STEPS_MAX_FINS = 5;                 // Maximale Anzahl Fins
    public static final int ALL_STEPS_MAX_ENDO_FINS = 2;            // Maximale Anzahl Endo-Fins
    public static final boolean ALL_STEPS_CHECK_TEMPLATES = true;   // Template-Check um Kandidaten von der Suche auszuschlie�en
    public static final int ALL_STEPS_MAX_KRAKEN_FISH_TYPE = 1;     // 0: nur basic, 1: basic+franken, 2: basic+franken+mutant
    public static final int ALL_STEPS_MIN_KRAKEN_FISH_SIZE = 2;     // number of units in base/cover sets
    public static final int ALL_STEPS_MAX_KRAKEN_FISH_SIZE = 4;     // number of units in base/cover sets
    public static final int ALL_STEPS_MAX_KRAKEN_FINS = 2;          // Maximale Anzahl Fins f�r Kraken-Suche
    public static final int ALL_STEPS_MAX_KRAKEN_ENDO_FINS = 0;     // Maximale Anzahl Endo-Fins f�r Kraken-Suche
    public static final String ALL_STEPS_FISH_CANDIDATES = "111111111";        // 1 for every candidate that should be searched, 0 otherwise
    public static final String ALL_STEPS_KRAKEN_FISH_CANDIDATES = "111111111"; // see above
    public static final int ALL_STEPS_SORT_MODE = 4; // sort by StepType
    private boolean allStepsSearchFish = ALL_STEPS_SEARCH_FISH;
    private int allStepsMaxFishType = ALL_STEPS_MAX_FISH_TYPE;
    private int allStepsMinFishSize = ALL_STEPS_MIN_FISH_SIZE;
    private int allStepsMaxFishSize = ALL_STEPS_MAX_FISH_SIZE;
    private int allStepsMaxFins = ALL_STEPS_MAX_FINS;
    private int allStepsMaxEndoFins = ALL_STEPS_MAX_ENDO_FINS;
    private boolean allStepsCheckTemplates = ALL_STEPS_CHECK_TEMPLATES;
    private int allStepsKrakenMaxFishType = ALL_STEPS_MAX_KRAKEN_FISH_TYPE;
    private int allStepsKrakenMinFishSize = ALL_STEPS_MIN_KRAKEN_FISH_SIZE;
    private int allStepsKrakenMaxFishSize = ALL_STEPS_MAX_KRAKEN_FISH_SIZE;
    private int allStepsMaxKrakenFins = ALL_STEPS_MAX_KRAKEN_FINS;
    private int allStepsMaxKrakenEndoFins = ALL_STEPS_MAX_KRAKEN_ENDO_FINS;
    private String allStepsFishCandidates = ALL_STEPS_FISH_CANDIDATES;
    private String allStepsKrakenFishCandidates = ALL_STEPS_KRAKEN_FISH_CANDIDATES;
    private int allStepsSortMode = ALL_STEPS_SORT_MODE;
    //SudokuPanel
    // Coloring Solver
    public static final Color[] COLORING_COLORS = {
        new Color(254, 204, 129), // 'a' - first color of first color pair
        new Color(252, 234, 190), // 'A' - second color of first color pair
        new Color(184, 184, 248), // 'b' - first color of second color pair
        new Color(220, 220, 252), // 'B' - second color of second color pair
        new Color(255, 185, 185), // 'c' - first color of third color pair
        new Color(255, 210, 210), // 'C' - second color of third color pair
        new Color(159, 247, 220), // 'd' - first color of fourth color pair
        new Color(206, 251, 237), // 'D' - second color of fourth color pair
        new Color(168, 255, 168), // 'e' - first color of fifth color pair
        new Color(215, 255, 215) // 'E' - second color of fifth color pair
    //        new Color(140, 198, 255),   // 'a' - first color of first color pair
    //        new Color(205, 171, 255),   // 'A' - second color of first color pair
    //        new Color(255, 164, 119),   // 'b' - first color of second color pair
    //        new Color(190, 124, 124),   // 'B' - second color of second color pair
    //        new Color(130, 130, 130),   // 'c' - first color of third color pair
    //        new Color(130,  30, 130),   // 'C' - second color of third color pair
    //        new Color(140, 140, 140),   // 'd' - first color of fourth color pair
    //        new Color(140,  40, 140),   // 'D' - second color of fourth color pair
    //        new Color(168, 255, 168),   // 'e' - first color of fifth color pair
    //        new Color(215, 255, 215)    // 'E' - second color of fifth color pair
    };
    public static final boolean COLOR_VALUES = true;
    private Color[] coloringColors = null;
    private boolean colorValues = COLOR_VALUES;
    // Single Digit Pattern Solver
    public static final boolean ALLOW_ERS_WITH_ONLY_TWO_CANDIDATES = false; // as it sais...
    private boolean allowErsWithOnlyTwoCandidates = ALLOW_ERS_WITH_ONLY_TWO_CANDIDATES;
    public static final boolean ALLOW_DUALS_AND_SIAMESE = false; // Dual 2-String-Kites, Dual Skyscrapers && Siamese Fish
    private boolean allowDualsAndSiamese = ALLOW_DUALS_AND_SIAMESE;
    // Uniqueness Solver
    public static final boolean ALLOW_UNIQUENESS_MISSING_CANDIDATES = true; // allow missing candidates in cells with additional candidates
    private boolean allowUniquenessMissingCandidates = ALLOW_UNIQUENESS_MISSING_CANDIDATES;
    // Allgemeines
    public static final boolean SHOW_CANDIDATES = true;    // alle Kandidaten anzeigen
    public static final boolean SHOW_WRONG_VALUES = true;  // Ung�ltige Zellen-/Kandidatenwerte anzeigen (Constraint-Verletzungen)
    public static final boolean SHOW_DEVIATIONS = true;    // Abweichungen von der richtigen L�sung anzeigen
    public static final boolean INVALID_CELLS = false;     // show possible cells
    public static final boolean SAVE_WINDOW_LAYOUT = true; // save window layout at shutdown
    public static final boolean USE_SHIFT_FOR_REGION_SELECT = true; // use shift for selecting cells or toggeling candidates
    public static final boolean ALTERNATIVE_MOUSE_MODE = false; // use simpler mouse mode (less clicks required)
    public static final boolean DELETE_CURSOR_DISPLAY = false; // let the cursor disappear after a while
    public static final int DELETE_CURSOR_DISPLAY_LENGTH = 1000; // time in ms
    public static final boolean USE_OR_INSTEAD_OF_AND_FOR_FILTER = false; // used when filtering more than one candidate
    public static final int DRAW_MODE = 1;
    //public static final int INITIAL_HEIGHT = 728;           // used to store window layout at shutdown
    public static final int INITIAL_HEIGHT = 844;           // used to store window layout at shutdown
    //public static final int INITIAL_WIDTH = 540;            // used to store window layout at shutdown
    public static final int INITIAL_WIDTH = 643;            // used to store window layout at shutdown
    public static final int INITIAL_VERT_DIVIDER_LOC = -1;  // used to store window layout at shutdown
    //public static final int INITIAL_HORZ_DIVIDER_LOC = 524; // used to store window layout at shutdown
    public static final int INITIAL_HORZ_DIVIDER_LOC = 627; // used to store window layout at shutdown
    public static final int INITIAL_DISP_MODE = 0;          // 0 .. sudoku only, 1 .. summary, 2 .. solution, 3 .. all steps
    public static final int INITIAL_X_POS = -1;             // used to store window layout at shutdown
    public static final int INITIAL_Y_POS = -1;             // used to store window layout at shutdown
    public static final boolean INITIAL_SHOW_HINT_PANEL = true;
    public static final boolean INITIAL_SHOW_TOOLBAR = true;
    public static final int ACT_LEVEL = DEFAULT_DIFFICULTY_LEVELS[1].getOrdinal(); // Standard is EASY
    public static final boolean SHOW_SUDOKU_SOLVED = false;
    private boolean showCandidates = SHOW_CANDIDATES;
    private boolean showWrongValues = SHOW_WRONG_VALUES;
    private boolean showDeviations = SHOW_DEVIATIONS;
    private boolean invalidCells = INVALID_CELLS;
    private boolean saveWindowLayout = SAVE_WINDOW_LAYOUT;
    private boolean useShiftForRegionSelect = USE_SHIFT_FOR_REGION_SELECT;
    private boolean alternativeMouseMode = ALTERNATIVE_MOUSE_MODE;
    private boolean deleteCursorDisplay = DELETE_CURSOR_DISPLAY;
    private int deleteCursorDisplayLength = DELETE_CURSOR_DISPLAY_LENGTH;
    private boolean useOrInsteadOfAndForFilter = USE_OR_INSTEAD_OF_AND_FOR_FILTER;
    private int drawMode = DRAW_MODE;
    private int initialHeight = INITIAL_HEIGHT;
    private int initialWidth = INITIAL_WIDTH;
    private int initialVertDividerLoc = INITIAL_VERT_DIVIDER_LOC;
    private int initialHorzDividerLoc = INITIAL_HORZ_DIVIDER_LOC;
    private int initialDisplayMode = INITIAL_DISP_MODE;
    private int initialXPos = INITIAL_X_POS;
    private int initialYPos = INITIAL_Y_POS;
    private boolean showHintPanel = INITIAL_SHOW_HINT_PANEL;
    private boolean showToolBar = INITIAL_SHOW_TOOLBAR;
    private int actLevel = ACT_LEVEL;
    private boolean showSudokuSolved = SHOW_SUDOKU_SOLVED;
    // Clipboard
    public static final boolean USE_ZERO_INSTEAD_OF_DOT = false; // as the name says...
    private boolean useZeroInsteadOfDot = USE_ZERO_INSTEAD_OF_DOT;
    // Farben und Fonts
    public static final Color GRID_COLOR = Color.BLACK;                                       // Zeichenfarbe f�r den Rahmen
    public static final Color INNER_GRID_COLOR = Color.LIGHT_GRAY;                            // Linien innerhalb des Rahmens
    public static final Color WRONG_VALUE_COLOR = Color.RED;                                  // Wert oder Kandidat an dieser Stelle nicht m�glich
    public static final Color DEVIATION_COLOR = new Color(255, 185, 185);                     // Wert oder Kandidat stimmt nicht mit L�sung �berein
    public static final Color CELL_FIXED_VALUE_COLOR = Color.BLACK;                           // vorgegebene Werte
    public static final Color CELL_VALUE_COLOR = Color.BLUE;                                  // korrekte selbst eingegebene Zellenwerte
    public static final Color CANDIDATE_COLOR = new Color(100, 100, 100);                     // korrekte Kandidaten
    public static final Color DEFAULT_CELL_COLOR = Color.WHITE;                               // Hintergrund normale Zelle
    public static final Color ALTERNATE_CELL_COLOR = Color.WHITE;                             // Hintergrund normale Zelle in jedem zweiten Block
    public static final Color AKT_CELL_COLOR = new Color(255, 255, 150);                      // Hintergrund aktuell markierte Zelle
    public static final Color INVALID_CELL_COLOR = new Color(255, 185, 185);                  // Hintergrund Zelle mit ung�ltigen Wert
    public static final Color POSSIBLE_CELL_COLOR = new Color(185, 255, 185);                 // Hintergrund Zelle mit m�glichem Wert
    public static final Color HINT_CANDIDATE_BACK_COLOR = new Color(113, 221, 137);           // Hintergrund Kandidat in Hinweis
    public static final Color HINT_CANDIDATE_DELETE_BACK_COLOR = new Color(249, 147, 162);    // Hintergrund f�r zu l�schende Kandidaten
    public static final Color HINT_CANDIDATE_CANNIBALISTIC_BACK_COLOR = new Color(255, 0, 0); // Hintergrund f�r zu l�schende Kandidaten
    public static final Color HINT_CANDIDATE_FIN_BACK_COLOR = new Color(140, 198, 255);       // Hintergrund f�r Fins
    public static final Color HINT_CANDIDATE_ENDO_FIN_BACK_COLOR = new Color(205, 171, 255);  // Hintergrund f�r Endo-Fins
    public static final Color HINT_CANDIDATE_COLOR = Color.BLACK;                             // Zeichenfarbe Kandidat in Hinweis
    public static final Color HINT_CANDIDATE_DELETE_COLOR = Color.BLACK;                      // Zeichenfarbe f�r zu l�schende Kandidaten
    public static final Color HINT_CANDIDATE_CANNIBALISTIC_COLOR = Color.BLACK;               // Zeichenfarbe f�r zu l�schende Kandidaten
    public static final Color HINT_CANDIDATE_FIN_COLOR = Color.BLACK;                         // Zeichenfarbe f�r Fins
    public static final Color HINT_CANDIDATE_ENDO_FIN_COLOR = Color.BLACK;                    // Zeichenfarbe f�r Endo-Fins
    public static final Color[] HINT_CANDIDATE_ALS_BACK_COLORS = { // Hintergrund f�r ALS (verschieden wegen Chains und Wings)
        new Color(215, 255, 215),
        new Color(255, 210, 210),
        new Color(206, 251, 237),
        new Color(252, 234, 190)
//        new Color(150, 150, 255),
//        new Color(150, 255, 150),
//        new Color(150, 100, 255),
//        new Color(150, 255, 100)
    };
    public static final Color[] HINT_CANDIDATE_ALS_COLORS = { // Zeichenfarbe f�r ALS-Candidaten
        Color.BLACK,
        Color.BLACK,
        Color.BLACK,
        Color.BLACK
    };
    public static final Color ARROW_COLOR = Color.RED;                                        // Farbe f�r Pfeile
    public static final double VALUE_FONT_FACTOR = 0.6;      // Zellengr��e * valueFontFactor gibt Schriftgr��e f�r Zellenwerte
    public static final double CANDIDATE_FONT_FACTOR = 0.25; // Zellengr��e * candidateFontFactor gibt Schriftgr��e f�r Kandidaten
    public static final double HINT_BACK_FACTOR = 1.6;       // um wie viel der Kreis beim Hint gr��er ist als die Zahl
    public static Font DEFAULT_VALUE_FONT = new Font("Tahoma", Font.PLAIN, 10);     // Standard f�r Zellenwerte (Gr��e wird ignoriert)
    public static Font DEFAULT_CANDIDATE_FONT = new Font("Tahoma", Font.PLAIN, 10); // Standard f�r Kandidaten (Gr��e wird ignoriert)
    public static Font BIG_FONT = new Font("Arial", Font.BOLD, 16);    // Font f�r Ausdruck �berschrift
    public static Font SMALL_FONT = new Font("Arial", Font.PLAIN, 10); // Font f�r Ausdruck Rating
    private Color gridColor = GRID_COLOR;
    private Color innerGridColor = INNER_GRID_COLOR;
    private Color wrongValueColor = WRONG_VALUE_COLOR;
    private Color deviationColor = DEVIATION_COLOR;
    private Color cellFixedValueColor = CELL_FIXED_VALUE_COLOR;
    private Color cellValueColor = CELL_VALUE_COLOR;
    private Color candidateColor = CANDIDATE_COLOR;
    private Color defaultCellColor = DEFAULT_CELL_COLOR;
    private Color alternateCellColor = ALTERNATE_CELL_COLOR;
    private Color aktCellColor = AKT_CELL_COLOR;
    private Color invalidCellColor = INVALID_CELL_COLOR;
    private Color possibleCellColor = POSSIBLE_CELL_COLOR;
    private Color hintCandidateBackColor = HINT_CANDIDATE_BACK_COLOR;
    private Color hintCandidateDeleteBackColor = HINT_CANDIDATE_DELETE_BACK_COLOR;
    private Color hintCandidateCannibalisticBackColor = HINT_CANDIDATE_CANNIBALISTIC_BACK_COLOR;
    private Color hintCandidateFinBackColor = HINT_CANDIDATE_FIN_BACK_COLOR;
    private Color hintCandidateEndoFinBackColor = HINT_CANDIDATE_ENDO_FIN_BACK_COLOR;
    private Color hintCandidateColor = HINT_CANDIDATE_COLOR;
    private Color hintCandidateDeleteColor = HINT_CANDIDATE_DELETE_COLOR;
    private Color hintCandidateCannibalisticColor = HINT_CANDIDATE_CANNIBALISTIC_COLOR;
    private Color hintCandidateFinColor = HINT_CANDIDATE_FIN_COLOR;
    private Color hintCandidateEndoFinColor = HINT_CANDIDATE_ENDO_FIN_COLOR;
    private Color[] hintCandidateAlsBackColors = null;
    private Color[] hintCandidateAlsColors = null;
    private Color arrowColor = ARROW_COLOR;
    private double valueFontFactor = VALUE_FONT_FACTOR;
    private double candidateFontFactor = CANDIDATE_FONT_FACTOR;
    private double hintBackFactor = HINT_BACK_FACTOR;
    private Font defaultValueFont = new Font(DEFAULT_VALUE_FONT.getName(), DEFAULT_VALUE_FONT.getStyle(), DEFAULT_VALUE_FONT.getSize());
    private Font defaultCandidateFont = new Font(DEFAULT_CANDIDATE_FONT.getName(), DEFAULT_CANDIDATE_FONT.getStyle(), DEFAULT_CANDIDATE_FONT.getSize());
    private Font bigFont = new Font(BIG_FONT.getName(), BIG_FONT.getStyle(), BIG_FONT.getSize());
    private Font smallFont = new Font(SMALL_FONT.getName(), SMALL_FONT.getStyle(), SMALL_FONT.getSize());
    public static final String DEFAULT_FILE_DIR = System.getProperty("user.home");
    private String defaultFileDir = DEFAULT_FILE_DIR;
    public static final String DEFAULT_LANGUAGE = "";
    private String language = DEFAULT_LANGUAGE;
    public static final String DEFAULT_LAF = "";
    private String laf = DEFAULT_LAF;
    // paint cursor only as small frame around cell
    public static final boolean ONLY_SMALL_CURSORS = false;
    public static final double CURSOR_FRAME_SIZE = 0.08;
    private boolean onlySmallCursors = ONLY_SMALL_CURSORS;
    private double cursorFrameSize = CURSOR_FRAME_SIZE;
    // game mode
    public static final GameMode GAME_MODE = GameMode.PLAYING;
    private GameMode gameMode = GAME_MODE;
    // show hint buttons in toolbar
    public static final boolean SHOW_HINT_BUTTONS_IN_TOOLBAR = false;
    private boolean showHintButtonsInToolbar = SHOW_HINT_BUTTONS_IN_TOOLBAR;
    // history of created puzzles and savepoints
    public static final int HISTORY_SIZE = 20;
    public static final boolean HISTORY_PREVIEW = true;
    private int historySize = HISTORY_SIZE;
    private boolean historyPreview = HISTORY_PREVIEW;
    private List<String> historyOfCreatedPuzzles = new ArrayList<String>(historySize);
    // BackdoorSearchDialog
    public static final boolean BDS_SEARCH_FOR_CELLS = true;       // Search for possible backdoor cells (or combinations of cells)
    public static final boolean BDS_SEARCH_FOR_CANDIDATES = false; // Search for possible backdoor candidates (or combinations of candidates)
    public static final int BDS_SEARCH_CANDIDATES_ANZ = 0;         // only single candidates
    private boolean bdsSearchForCells = BDS_SEARCH_FOR_CELLS;
    private boolean bdsSearchForCandidates = BDS_SEARCH_FOR_CANDIDATES;
    private int bdsSearchCandidatesAnz = BDS_SEARCH_CANDIDATES_ANZ;
    // Generator Patterns: List is empty per default
    public static final int GENERATOR_PATTERN_INDEX = -1;
    private ArrayList<GeneratorPattern> generatorPatterns = new ArrayList<GeneratorPattern>();
    private int generatorPatternIndex = GENERATOR_PATTERN_INDEX;
    //Singleton
    public static Options instance = null;

    /** Creates a new instance of Options */
    public Options() {
        difficultyLevels = copyDifficultyLevels(DEFAULT_DIFFICULTY_LEVELS);
        orgSolverSteps = copyStepConfigs(DEFAULT_SOLVER_STEPS, false, false, true);
        solverSteps = copyStepConfigs(DEFAULT_SOLVER_STEPS, false, false, false);
        solverStepsProgress = copyStepConfigs(DEFAULT_SOLVER_STEPS, false, false, false, true);

        hintCandidateAlsBackColors = new Color[HINT_CANDIDATE_ALS_BACK_COLORS.length];
        for (int i = 0; i < HINT_CANDIDATE_ALS_BACK_COLORS.length; i++) {
            hintCandidateAlsBackColors[i] = new Color(HINT_CANDIDATE_ALS_BACK_COLORS[i].getRGB());
        }
        hintCandidateAlsColors = new Color[HINT_CANDIDATE_ALS_COLORS.length];
        for (int i = 0; i < HINT_CANDIDATE_ALS_COLORS.length; i++) {
            hintCandidateAlsColors[i] = new Color(HINT_CANDIDATE_ALS_COLORS[i].getRGB());
        }
        coloringColors = new Color[COLORING_COLORS.length];
        for (int i = 0; i < COLORING_COLORS.length; i++) {
            coloringColors[i] = new Color(COLORING_COLORS[i].getRGB());
        }

//    public static final Font DEFAULT_VALUE_FONT = new Font("Tahoma", Font.PLAIN, 10);     // Standard f�r Zellenwerte (Gr��e wird ignoriert)
//    public static final Font DEFAULT_CANDIDATE_FONT = new Font("Tahoma", Font.PLAIN, 10); // Standard f�r Kandidaten (Gr��e wird ignoriert)
//    public static final Font BIG_FONT = new Font("Arial", Font.BOLD, 16);    // Font f�r Ausdruck �berschrift
//    public static final Font SMALL_FONT = new Font("Arial", Font.PLAIN, 10); // Font f�r Ausdruck Rating
        // allow for different fonts in different OSes
        String[] fontNames = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        if (!checkFont(DEFAULT_CANDIDATE_FONT, fontNames)) {
            DEFAULT_CANDIDATE_FONT = new Font(Font.SANS_SERIF, DEFAULT_CANDIDATE_FONT.getStyle(), DEFAULT_CANDIDATE_FONT.getSize());
            defaultCandidateFont = new Font(DEFAULT_CANDIDATE_FONT.getName(), DEFAULT_CANDIDATE_FONT.getStyle(), DEFAULT_CANDIDATE_FONT.getSize());
        }
        if (!checkFont(DEFAULT_VALUE_FONT, fontNames)) {
            DEFAULT_VALUE_FONT = new Font(Font.SANS_SERIF, DEFAULT_VALUE_FONT.getStyle(), DEFAULT_VALUE_FONT.getSize());
            defaultValueFont = new Font(DEFAULT_VALUE_FONT.getName(), DEFAULT_VALUE_FONT.getStyle(), DEFAULT_VALUE_FONT.getSize());
        }
        if (!checkFont(defaultCandidateFont, fontNames)) {
            defaultCandidateFont = new Font(DEFAULT_CANDIDATE_FONT.getName(), DEFAULT_CANDIDATE_FONT.getStyle(), DEFAULT_CANDIDATE_FONT.getSize());
        }
        if (!checkFont(defaultValueFont, fontNames)) {
            defaultValueFont = new Font(DEFAULT_VALUE_FONT.getName(), DEFAULT_VALUE_FONT.getStyle(), DEFAULT_VALUE_FONT.getSize());
        }
        if (!checkFont(BIG_FONT, fontNames)) {
            BIG_FONT = new Font(Font.SANS_SERIF, BIG_FONT.getStyle(), BIG_FONT.getSize());
            bigFont = new Font(BIG_FONT.getName(), BIG_FONT.getStyle(), BIG_FONT.getSize());
        }
        if (!checkFont(SMALL_FONT, fontNames)) {
            SMALL_FONT = new Font(Font.SANS_SERIF, SMALL_FONT.getStyle(), SMALL_FONT.getSize());
            smallFont = new Font(SMALL_FONT.getName(), SMALL_FONT.getStyle(), SMALL_FONT.getSize());
        }
        if (!checkFont(bigFont, fontNames)) {
            bigFont = new Font(BIG_FONT.getName(), BIG_FONT.getStyle(), BIG_FONT.getSize());
        }
        if (!checkFont(smallFont, fontNames)) {
            smallFont = new Font(SMALL_FONT.getName(), SMALL_FONT.getStyle(), SMALL_FONT.getSize());
        }
    }

    private boolean checkFont(Font font, String[] fontNames) {
        if (Arrays.binarySearch(fontNames, font.getName()) >= 0) {
            return true;
        }
        return false;
    }

    /**
     * Reset all options to their default values by simply creating
     * an new instance. {@link #getInstance() } must
     * be called afterwards to get the new options.
     */
    public static void resetAll() {
        instance = new Options();
    }

    public static Options getInstance() {
        if (instance == null) {
            readOptions();
        }
        return instance;
    }

    public DifficultyLevel[] copyDifficultyLevels(DifficultyLevel[] src) {
        DifficultyLevel[] dest = new DifficultyLevel[src.length];
        for (int i = 0; i < src.length; i++) {
            DifficultyLevel act = src[i];
            dest[i] = new DifficultyLevel(act.getType(), act.getMaxScore(),
                    act.getName(), act.getBackgroundColor(), act.getForegroundColor());
        }
        return dest;
    }

    public StepConfig[] copyStepConfigs(StepConfig[] src, boolean noLastTwo, boolean addLastTwo) {
        return copyStepConfigs(src, noLastTwo, addLastTwo, false);
    }

    public StepConfig[] copyStepConfigs(StepConfig[] src, boolean noLastTwo, boolean addLastTwo, boolean noSort) {
        return copyStepConfigs(src, noLastTwo, addLastTwo, noSort, false);
    }

    public StepConfig[] copyStepConfigs(StepConfig[] src, boolean noLastTwo,
            boolean addLastTwo, boolean noSort, boolean sortProgress) {
        // Wenn noLastTwo oder addLastTwo gesetzt sind, ist src bereits sortiert, das hei�t
        // INCOMPLETE und GIVE_UP stehen ganz hinten
        // That's not true if src == DEFAULT_SOLVER_STEPS! (reset in ConfigSolverPanel)
        int length = src.length;
        if (noLastTwo) {
            length -= 2;
        }
        if (addLastTwo) {
            length += 2;
        }
        StepConfig[] dest = new StepConfig[length];
        // let's do it the hard way: When "reset" is pressed in ConfigSolverPanel, everything is copied
        // from DEFAULT_SOLVER_STEPS, noLastTwo is set, addLastTwo is not set -> INCOMPLETE and GIVE_UP
        // are the first two elements
        if (src == DEFAULT_SOLVER_STEPS && noLastTwo == true && addLastTwo == false && noSort == false) {
            for (int i = 0; i < length; i++) {
                StepConfig act = src[i + 2];
                dest[i] = new StepConfig(act.getIndex(), act.getType(), act.getLevel(), act.getCategory(),
                        act.getBaseScore(), act.getAdminScore(), act.isEnabled(), act.isAllStepsEnabled(),
                        act.getIndexProgress(), act.isEnabledProgress(), act.isEnabledTraining());
            }
        } else {
            for (int i = 0; i < (addLastTwo ? length - 2 : length); i++) {
                StepConfig act = src[i];
                dest[i] = new StepConfig(act.getIndex(), act.getType(), act.getLevel(), act.getCategory(),
                        act.getBaseScore(), act.getAdminScore(), act.isEnabled(), act.isAllStepsEnabled(),
                        act.getIndexProgress(), act.isEnabledProgress(), act.isEnabledTraining());
            }
        }
        if (addLastTwo) {
            StepConfig act = DEFAULT_SOLVER_STEPS[0];
            dest[dest.length - 2] = new StepConfig(act.getIndex(), act.getType(), act.getLevel(), act.getCategory(),
                    act.getBaseScore(), act.getAdminScore(), act.isEnabled(), act.isAllStepsEnabled(),
                    act.getIndexProgress(), act.isEnabledProgress(), act.isEnabledTraining());
            act = DEFAULT_SOLVER_STEPS[1];
            dest[dest.length - 1] = new StepConfig(act.getIndex(), act.getType(), act.getLevel(), act.getCategory(),
                    act.getBaseScore(), act.getAdminScore(), act.isEnabled(), act.isAllStepsEnabled(),
                    act.getIndexProgress(), act.isEnabledProgress(), act.isEnabledTraining());
        }
        if (!noSort) {
            if (sortProgress) {
                Arrays.sort(dest, progressComparator);
            } else {
                Arrays.sort(dest);
            }
        }
        return dest;
    }

    /**
     * Alle �nderungen in solverSteps werden in orgSolverSteps �bernommen, orgSolverSteps bleibt
     * allerdings weiterhin unsortiert (f�r XmlWriter)
     */
    public void adjustOrgSolverSteps() {
        boolean somethingChanged = false;
        for (StepConfig step : solverSteps) {
            StepConfig orgStep = null;
            for (int i = 0; i < orgSolverSteps.length; i++) {
                if (orgSolverSteps[i].getType() == step.getType()) {
                    orgStep = orgSolverSteps[i];
                    break;
                }
            }
            if (orgStep == null) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING, "StepConfig not found!");
                continue;
            }
            if (step.getAdminScore() != orgStep.getAdminScore()
                    || step.getBaseScore() != orgStep.getBaseScore()
                    || step.getCategory() != orgStep.getCategory()
                    || step.isEnabled() != orgStep.isEnabled()
                    || step.getIndex() != orgStep.getIndex()
                    || step.getLevel() != orgStep.getLevel()) {
                somethingChanged = true;
            }
            orgStep.setAdminScore(step.getAdminScore());
            orgStep.setBaseScore(step.getBaseScore());
            orgStep.setCategory(step.getCategory());
            orgStep.setEnabled(step.isEnabled());
            orgStep.setIndex(step.getIndex());
            orgStep.setLevel(step.getLevel());
            // values for allStepsEnabled, indexHeuristics, enabledHeuristics and
            // enableTraining are not set here, is done manually in the
            // corresponding config panel
        }
        if (somethingChanged) {
            BackgroundGeneratorThread.getInstance().resetAll();
        }
    }

    /**
     * Resort the progressSteps (needed after options change)
     */
    public void sortProgressSteps() {
        Arrays.sort(solverStepsProgress, progressComparator);
    }

    /**
     * Since the local is set AFTER the options have been read, the names of
     * the difficulty levels are always in the default local. They have to
     * be adjusted after the correct locale has been set.
     */
    public void resetDifficultyLevelStrings() {
//        new DifficultyLevel(DifficultyType.INCOMPLETE, 0, java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.incomplete"), Color.BLACK, Color.WHITE),
//        new DifficultyLevel(DifficultyType.EASY, 600, java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.easy"), Color.WHITE, Color.BLACK),
//        new DifficultyLevel(DifficultyType.MEDIUM, 1500, java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.medium"), new Color(100, 255, 100), Color.BLACK),
//        new DifficultyLevel(DifficultyType.HARD, 3500, java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.hard"), new Color(255, 255, 100), Color.BLACK),
//        new DifficultyLevel(DifficultyType.UNFAIR, 5000, java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.unfair"), new Color(255, 150, 80), Color.BLACK),
//        new DifficultyLevel(DifficultyType.EXTREME, Integer.MAX_VALUE, java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.extreme"), new Color(255, 100, 100), Color.BLACK)
        DEFAULT_DIFFICULTY_LEVELS[0].setName(java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.incomplete"));
        DEFAULT_DIFFICULTY_LEVELS[1].setName(java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.easy"));
        DEFAULT_DIFFICULTY_LEVELS[2].setName(java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.medium"));
        DEFAULT_DIFFICULTY_LEVELS[3].setName(java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.hard"));
        DEFAULT_DIFFICULTY_LEVELS[4].setName(java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.unfair"));
        DEFAULT_DIFFICULTY_LEVELS[5].setName(java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.extreme"));
        difficultyLevels[0].setName(java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.incomplete"));
        difficultyLevels[1].setName(java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.easy"));
        difficultyLevels[2].setName(java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.medium"));
        difficultyLevels[3].setName(java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.hard"));
        difficultyLevels[4].setName(java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.unfair"));
        difficultyLevels[5].setName(java.util.ResourceBundle.getBundle("intl/MainFrame").getString("MainFrame.extreme"));

    }

    /**
     * Returns a String that contains a comma seperated list of all steps,
     * that are configured for training mode.
     * @param ellipsis
     * @return
     */
    public String getTrainingStepsString(boolean ellipsis) {
        return getTrainingStepsString(orgSolverSteps, ellipsis);
    }

    /**
     * Returns a String that contains a comma seperated list of all steps,
     * that are configured for training mode.<br>
     * If ellipsis is <cde>true</code>, only one technique us shown. If more
     * than one technique is selected, an ellipsis is appended to the
     * first technique.
     *
     * @param stepArray
     * @param ellipsis
     * @return
     */
    public String getTrainingStepsString(StepConfig[] stepArray, boolean ellipsis) {
        StringBuilder tmp = new StringBuilder();
        boolean first = true;
        for (StepConfig step : stepArray) {
            if (step.isEnabledTraining()) {
                if (first) {
                    first = false;
                } else {
                    if (ellipsis) {
                        tmp.append("...");
                        break;
                    } else {
                        tmp.append(", ");
                    }
                }
                tmp.append(step.getType().getStepName());
            }
        }
        return tmp.toString();
    }

    public void writeOptions() throws FileNotFoundException {
        String tmp = System.getProperty("java.io.tmpdir");
        String fileName = null;
        if (tmp.endsWith(File.separator)) {
            fileName = tmp + FILE_NAME;
        } else {
            fileName = tmp + File.separator + FILE_NAME;
        }
//        readOptions(System.getProperty("java.io.tmpdir") + File.separator + FILE_NAME);
        writeOptions(fileName);
    }

    public void writeOptions(String fileName) throws FileNotFoundException {
        Logger.getLogger(Options.class.getName()).log(Level.INFO, "Writing options to {0}", fileName);
        XMLEncoder out = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(fileName)));
        out.writeObject(this);
        out.close();
    }

    public static void readOptions() {
        String tmp = System.getProperty("java.io.tmpdir");
        String fileName = null;
        if (tmp.endsWith(File.separator)) {
            fileName = tmp + FILE_NAME;
        } else {
            fileName = tmp + File.separator + FILE_NAME;
        }
//        readOptions(System.getProperty("java.io.tmpdir") + File.separator + FILE_NAME);
        readOptions(fileName);
    }

    public static void readOptions(String fileName) {
        Logger.getLogger(Options.class.getName()).log(Level.INFO, "Reading options from {0}", fileName);
        try {
            XMLDecoder in = new XMLDecoder(new BufferedInputStream(new FileInputStream(fileName)));
            instance = (Options) in.readObject();
            in.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Options.class.getName()).log(Level.INFO, "No config file found");
            // es gibt noch keine Options-Datei
            instance = new Options();
            try {
                // neue anlegen
                instance.writeOptions();
            } catch (FileNotFoundException exi) {
                Logger.getLogger(Options.class.getName()).log(Level.SEVERE, "Error writing options", exi);
            }
        }
        // readObject() passt nur orgSolverSteps an,
        // nicht aber solverSteps -> neu kopieren!
        // the same for solverStepsProgress
        instance.solverSteps = instance.copyStepConfigs(instance.orgSolverSteps, false, false, false);
        instance.solverStepsProgress = instance.copyStepConfigs(instance.orgSolverSteps, false, false, false, true);
    }

    @SuppressWarnings("CallToThreadDumpStack")
    public static void main(String[] args) {
        Options options = new Options();
        try {
            XMLEncoder out = new XMLEncoder(new BufferedOutputStream(new FileOutputStream("L:\\dummy.xml")));
            out.writeObject(options);
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        try {
            XMLDecoder in = new XMLDecoder(new BufferedInputStream(new FileInputStream("L:\\dummy.xml")));
            //out.setPersistenceDelegate(StepConfig[].class, new SolverStepsPersistenceDelegate());
            options = (Options) in.readObject();
            in.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println(options.solverSteps.length);
        for (int i = 0; i < options.solverSteps.length; i++) {
            System.out.println(i + ": " + options.solverSteps[i]);
        }
    }

    /**
     * @return the historyOfCreatedPuzzles
     */
    public List<String> getHistoryOfCreatedPuzzles() {
        return historyOfCreatedPuzzles;
    }

    /**
     * @param historyOfCreatedPuzzles the historyOfCreatedPuzzles to set
     */
    public void setHistoryOfCreatedPuzzles(List<String> historyOfCreatedPuzzles) {
        this.historyOfCreatedPuzzles = historyOfCreatedPuzzles;
    }

    /**
     * @return the historyPreview
     */
    public boolean isHistoryPreview() {
        return historyPreview;
    }

    /**
     * @param historyPreview the historyPreview to set
     */
    public void setHistoryPreview(boolean historyPreview) {
        this.historyPreview = historyPreview;
    }

    /**
     * @return the invalidCells
     */
    public boolean isInvalidCells() {
        return invalidCells;
    }

    /**
     * @param invalidCells the invalidCells to set
     */
    public void setInvalidCells(boolean invalidCells) {
        this.invalidCells = invalidCells;
    }

    /**
     * @return the allStepsSortMode
     */
    public int getAllStepsSortMode() {
        return allStepsSortMode;
    }

    /**
     * @param allStepsSortMode the allStepsSortMode to set
     */
    public void setAllStepsSortMode(int allStepsSortMode) {
        this.allStepsSortMode = allStepsSortMode;
    }

    /**
     * @return the bdsSearchForCells
     */
    public boolean isBdsSearchForCells() {
        return bdsSearchForCells;
    }

    /**
     * @param bdsSearchForCells the bdsSearchForCells to set
     */
    public void setBdsSearchForCells(boolean bdsSearchForCells) {
        this.bdsSearchForCells = bdsSearchForCells;
    }

    /**
     * @return the bdsSearchForCandidates
     */
    public boolean isBdsSearchForCandidates() {
        return bdsSearchForCandidates;
    }

    /**
     * @param bdsSearchForCandidates the bdsSearchForCandidates to set
     */
    public void setBdsSearchForCandidates(boolean bdsSearchForCandidates) {
        this.bdsSearchForCandidates = bdsSearchForCandidates;
    }

    /**
     * @return the bdsSearchCandidatesAnz
     */
    public int getBdsSearchCandidatesAnz() {
        return bdsSearchCandidatesAnz;
    }

    /**
     * @param bdsSearchCandidatesAnz the bdsSearchCandidatesAnz to set
     */
    public void setBdsSearchCandidatesAnz(int bdsSearchCandidatesAnz) {
        this.bdsSearchCandidatesAnz = bdsSearchCandidatesAnz;
    }

    /**
     * @return the fishDisplayMode
     */
    public int getFishDisplayMode() {
        return fishDisplayMode;
    }

    /**
     * @param fishDisplayMode the fishDisplayMode to set
     */
    public void setFishDisplayMode(int fishDisplayMode) {
        this.fishDisplayMode = fishDisplayMode;
    }

    /**
     * @return the useShiftForRegionSelect
     */
    public boolean isUseShiftForRegionSelect() {
        return useShiftForRegionSelect;
    }

    /**
     * @param useShiftForRegionSelect the useShiftForRegionSelect to set
     */
    public void setUseShiftForRegionSelect(boolean useShiftForRegionSelect) {
        this.useShiftForRegionSelect = useShiftForRegionSelect;
    }

    /**
     * @return the allowUniquenessMissingCandidates
     */
    public boolean isAllowUniquenessMissingCandidates() {
        return allowUniquenessMissingCandidates;
    }

    /**
     * @param allowUniquenessMissingCandidates the allowUniquenessMissingCandidates to set
     */
    public void setAllowUniquenessMissingCandidates(boolean allowUniquenessMissingCandidates) {
        this.allowUniquenessMissingCandidates = allowUniquenessMissingCandidates;
    }

    /**
     * @return the onlySmallCursors
     */
    public boolean isOnlySmallCursors() {
        return onlySmallCursors;
    }

    /**
     * @param onlySmallCursors the onlySmallCursors to set
     */
    public void setOnlySmallCursors(boolean onlySmallCursors) {
        this.onlySmallCursors = onlySmallCursors;
    }

    /**
     * @return the cursorFrameSize
     */
    public double getCursorFrameSize() {
        return cursorFrameSize;
    }

    /**
     * @param cursorFrameSize the cursorFrameSize to set
     */
    public void setCursorFrameSize(double cursorFrameSize) {
        this.cursorFrameSize = cursorFrameSize;
    }

    /**
     * @return the gameMode
     */
    public GameMode getGameMode() {
        return gameMode;
    }

    /**
     * @param gameMode the gameMode to set
     */
    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    /**
     * @return the showHintButtonsInToolbar
     */
    public boolean isShowHintButtonsInToolbar() {
        return showHintButtonsInToolbar;
    }

    /**
     * @param showHintButtonsInToolbar the showHintButtonsInToolbar to set
     */
    public void setShowHintButtonsInToolbar(boolean showHintButtonsInToolbar) {
        this.showHintButtonsInToolbar = showHintButtonsInToolbar;
    }

    /**
     * @return the colorValues
     */
    public boolean isColorValues() {
        return colorValues;
    }

    /**
     * @param colorValues the colorValues to set
     */
    public void setColorValues(boolean colorValues) {
        this.colorValues = colorValues;
    }

    /**
     * @return the alternativeMouseMode
     */
    public boolean isAlternativeMouseMode() {
        return alternativeMouseMode;
    }

    /**
     * @param alternativeMouseMode the alternativeMouseMode to set
     */
    public void setAlternativeMouseMode(boolean alternativeMouseMode) {
        this.alternativeMouseMode = alternativeMouseMode;
    }

    /**
     * @return the actLevel
     */
    public int getActLevel() {
//        System.out.println("getActLevel(" + actLevel + ")");
//        Thread.dumpStack();
        return actLevel;
    }

    /**
     * @param actLevel the actLevel to set
     */
    public void setActLevel(int actLevel) {
//        System.out.println("setActLevel(" + actLevel + ")");
//        Thread.dumpStack();
        this.actLevel = actLevel;
    }

    /**
     * @return the normalPuzzles
     */
    public String[][] getNormalPuzzles() {
        return normalPuzzles;
    }

    /**
     * @param normalPuzzles the normalPuzzles to set
     */
    public void setNormalPuzzles(String[][] normalPuzzles) {
        this.normalPuzzles = normalPuzzles;
    }

    /**
     * @return the learningPuzzles
     */
    public String[] getLearningPuzzles() {
        return learningPuzzles;
    }

    /**
     * @param learningPuzzles the learningPuzzles to set
     */
    public void setLearningPuzzles(String[] learningPuzzles) {
        this.learningPuzzles = learningPuzzles;
    }

    /**
     * @return the practisingPuzzles
     */
    public String[] getPractisingPuzzles() {
        return practisingPuzzles;
    }

    /**
     * @param practisingPuzzles the practisingPuzzles to set
     */
    public void setPractisingPuzzles(String[] practisingPuzzles) {
        this.practisingPuzzles = practisingPuzzles;
    }

    /**
     * @return the practisingPuzzlesLevel
     */
    public int getPractisingPuzzlesLevel() {
        return practisingPuzzlesLevel;
    }

    /**
     * @param practisingPuzzlesLevel the practisingPuzzlesLevel to set
     */
    public void setPractisingPuzzlesLevel(int practisingPuzzlesLevel) {
        this.practisingPuzzlesLevel = practisingPuzzlesLevel;
    }

    /**
     * @return the generatorPatterns
     */
    public ArrayList<GeneratorPattern> getGeneratorPatterns() {
        return generatorPatterns;
    }

    /**
     * @param generatorPatterns the generatorPatterns to set
     */
    public void setGeneratorPatterns(ArrayList<GeneratorPattern> generatorPatterns) {
        this.generatorPatterns = generatorPatterns;
    }

    /**
     * @return the generatorPatternIndex
     */
    public int getGeneratorPatternIndex() {
        return generatorPatternIndex;
    }

    /**
     * @param generatorPatternIndex the generatorPatternIndex to set
     */
    public void setGeneratorPatternIndex(int generatorPatternIndex) {
        this.generatorPatternIndex = generatorPatternIndex;
    }

    /**
     * @return the showSudokuSolved
     */
    public boolean isShowSudokuSolved() {
        return showSudokuSolved;
    }

    /**
     * @param showSudokuSolved the showSudokuSolved to set
     */
    public void setShowSudokuSolved(boolean showSudokuSolved) {
        this.showSudokuSolved = showSudokuSolved;
    }

    /**
     * @return the deleteCursorDisplay
     */
    public boolean isDeleteCursorDisplay() {
        return deleteCursorDisplay;
    }

    /**
     * @param deleteCursorDisplay the deleteCursorDisplay to set
     */
    public void setDeleteCursorDisplay(boolean deleteCursorDisplay) {
        this.deleteCursorDisplay = deleteCursorDisplay;
    }

    /**
     * @return the deleteCursorDisplayLength
     */
    public int getDeleteCursorDisplayLength() {
        return deleteCursorDisplayLength;
    }

    /**
     * @param deleteCursorDisplayLength the deleteCursorDisplayLength to set
     */
    public void setDeleteCursorDisplayLength(int deleteCursorDisplayLength) {
        this.deleteCursorDisplayLength = deleteCursorDisplayLength;
    }

    /**
     * @return the alternateCellColor
     */
    public Color getAlternateCellColor() {
        return alternateCellColor;
    }

    /**
     * @param alternateCellColor the alternateCellColor to set
     */
    public void setAlternateCellColor(Color alternateCellColor) {
        this.alternateCellColor = alternateCellColor;
    }

    /**
     * @return the useOrInsteadOfAndForFilter
     */
    public boolean isUseOrInsteadOfAndForFilter() {
        return useOrInsteadOfAndForFilter;
    }

    /**
     * @param useOrInsteadOfAndForFilter the useOrInsteadOfAndForFilter to set
     */
    public void setUseOrInsteadOfAndForFilter(boolean useOrInsteadOfAndForFilter) {
        this.useOrInsteadOfAndForFilter = useOrInsteadOfAndForFilter;
    }

    private static class ProgressComparator implements Comparator<StepConfig> {

        @Override
        public int compare(StepConfig o1, StepConfig o2) {
            return o1.getIndexProgress() - o2.getIndexProgress();
        }
    }

    public DifficultyLevel nextDifficultyLevel(DifficultyLevel level) {
        int i = 0;
        for (i = 0; i < difficultyLevels.length; i++) {
            if (level == difficultyLevels[i]) {
                break;
            }
        }
        if (i >= difficultyLevels.length - 1) {
            return null;
        } else {
            return difficultyLevels[i + 1];
        }
    }

    /**
     * Find a {@link DifficultyLevel} via its ordinal.
     * @param ordinal
     * @return
     */
    public DifficultyLevel getDifficultyLevel(int ordinal) {
        int i = 0;
        for (i = 0; i < difficultyLevels.length; i++) {
            if (ordinal == difficultyLevels[i].getOrdinal()) {
                break;
            }
        }
        if (i >= difficultyLevels.length) {
            return null;
        } else {
            return difficultyLevels[i];
        }
    }

    public StepConfig[] getOrgSolverSteps() {
        return orgSolverSteps;
    }

    public void setOrgSolverSteps(StepConfig[] orgSolverSteps) {
        this.orgSolverSteps = orgSolverSteps;
    }

    public int getRestrictChainLength() {
        return restrictChainLength;
    }

    public void setRestrictChainLength(int restrictChainLength) {
        this.restrictChainLength = restrictChainLength;
    }

    public int getRestrictNiceLoopLength() {
        return restrictNiceLoopLength;
    }

    public void setRestrictNiceLoopLength(int restrictNiceLoopLength) {
        this.restrictNiceLoopLength = restrictNiceLoopLength;
    }

    public boolean isRestrictChainSize() {
        return restrictChainSize;
    }

    public void setRestrictChainSize(boolean restrictChainSize) {
        this.restrictChainSize = restrictChainSize;
    }

    public int getMaxFins() {
        return maxFins;
    }

    public void setMaxFins(int maxFins) {
        this.maxFins = maxFins;
    }

    public int getMaxEndoFins() {
        return maxEndoFins;
    }

    public void setMaxEndoFins(int maxEndoFins) {
        this.maxEndoFins = maxEndoFins;
    }

    public boolean isCheckTemplates() {
        return checkTemplates;
    }

    public void setCheckTemplates(boolean checkTemplates) {
        this.checkTemplates = checkTemplates;
    }

    public boolean isShowCandidates() {
        return showCandidates;
    }

    public void setShowCandidates(boolean showCandidates) {
        this.showCandidates = showCandidates;
    }

    public boolean isShowWrongValues() {
        return showWrongValues;
    }

    public void setShowWrongValues(boolean showWrongValues) {
        this.showWrongValues = showWrongValues;
    }

    public boolean isShowDeviations() {
        return showDeviations;
    }

    public void setShowDeviations(boolean showDeviations) {
        this.showDeviations = showDeviations;
    }

    public int getDrawMode() {
        return drawMode;
    }

    public void setDrawMode(int drawMode) {
        this.drawMode = drawMode;
    }

    public Color getGridColor() {
        return gridColor;
    }

    public void setGridColor(Color gridColor) {
        this.gridColor = gridColor;
    }

    public Color getInnerGridColor() {
        return innerGridColor;
    }

    public void setInnerGridColor(Color innerGridColor) {
        this.innerGridColor = innerGridColor;
    }

    public Color getWrongValueColor() {
        return wrongValueColor;
    }

    public void setWrongValueColor(Color wrongValueColor) {
        this.wrongValueColor = wrongValueColor;
    }

    public Color getDeviationColor() {
        return deviationColor;
    }

    public void setDeviationColor(Color deviationColor) {
        this.deviationColor = deviationColor;
    }

    public Color getCellFixedValueColor() {
        return cellFixedValueColor;
    }

    public void setCellFixedValueColor(Color cellFixedValueColor) {
        this.cellFixedValueColor = cellFixedValueColor;
    }

    public Color getCellValueColor() {
        return cellValueColor;
    }

    public void setCellValueColor(Color cellValueColor) {
        this.cellValueColor = cellValueColor;
    }

    public Color getCandidateColor() {
        return candidateColor;
    }

    public void setCandidateColor(Color candidateColor) {
        this.candidateColor = candidateColor;
    }

    public Color getDefaultCellColor() {
        return defaultCellColor;
    }

    public void setDefaultCellColor(Color defaultCellColor) {
        this.defaultCellColor = defaultCellColor;
    }

    public Color getAktCellColor() {
        return aktCellColor;
    }

    public void setAktCellColor(Color aktCellColor) {
        this.aktCellColor = aktCellColor;
    }

    public Color getInvalidCellColor() {
        return invalidCellColor;
    }

    public void setInvalidCellColor(Color invalidCellColor) {
        this.invalidCellColor = invalidCellColor;
    }

    public Color getPossibleCellColor() {
        return possibleCellColor;
    }

    public void setPossibleCellColor(Color possibleCellColor) {
        this.possibleCellColor = possibleCellColor;
    }

    public Color getHintCandidateBackColor() {
        return hintCandidateBackColor;
    }

    public void setHintCandidateBackColor(Color hintCandidateBackColor) {
        this.hintCandidateBackColor = hintCandidateBackColor;
    }

    public Color getHintCandidateDeleteBackColor() {
        return hintCandidateDeleteBackColor;
    }

    public void setHintCandidateDeleteBackColor(Color hintCandidateDeleteBackColor) {
        this.hintCandidateDeleteBackColor = hintCandidateDeleteBackColor;
    }

    public Color getHintCandidateCannibalisticBackColor() {
        return hintCandidateCannibalisticBackColor;
    }

    public void setHintCandidateCannibalisticBackColor(Color hintCandidateCannibalisticBackColor) {
        this.hintCandidateCannibalisticBackColor = hintCandidateCannibalisticBackColor;
    }

    public Color getHintCandidateFinBackColor() {
        return hintCandidateFinBackColor;
    }

    public void setHintCandidateFinBackColor(Color hintCandidateFinBackColor) {
        this.hintCandidateFinBackColor = hintCandidateFinBackColor;
    }

    public Color getHintCandidateEndoFinBackColor() {
        return hintCandidateEndoFinBackColor;
    }

    public void setHintCandidateEndoFinBackColor(Color hintCandidateEndoFinBackColor) {
        this.hintCandidateEndoFinBackColor = hintCandidateEndoFinBackColor;
    }

    public Color getHintCandidateColor() {
        return hintCandidateColor;
    }

    public void setHintCandidateColor(Color hintCandidateColor) {
        this.hintCandidateColor = hintCandidateColor;
    }

    public Color getHintCandidateDeleteColor() {
        return hintCandidateDeleteColor;
    }

    public void setHintCandidateDeleteColor(Color hintCandidateDeleteColor) {
        this.hintCandidateDeleteColor = hintCandidateDeleteColor;
    }

    public Color getHintCandidateCannibalisticColor() {
        return hintCandidateCannibalisticColor;
    }

    public void setHintCandidateCannibalisticColor(Color hintCandidateCannibalisticColor) {
        this.hintCandidateCannibalisticColor = hintCandidateCannibalisticColor;
    }

    public Color getHintCandidateFinColor() {
        return hintCandidateFinColor;
    }

    public void setHintCandidateFinColor(Color hintCandidateFinColor) {
        this.hintCandidateFinColor = hintCandidateFinColor;
    }

    public Color getHintCandidateEndoFinColor() {
        return hintCandidateEndoFinColor;
    }

    public void setHintCandidateEndoFinColor(Color hintCandidateEndoFinColor) {
        this.hintCandidateEndoFinColor = hintCandidateEndoFinColor;
    }

    public Color[] getHintCandidateAlsBackColors() {
        return hintCandidateAlsBackColors;
    }

    public void setHintCandidateAlsBackColors(Color[] hintCandidateAlsBackColors) {
        this.hintCandidateAlsBackColors = hintCandidateAlsBackColors;
    }

    public Color[] getHintCandidateAlsColors() {
        return hintCandidateAlsColors;
    }

    public void setHintCandidateAlsColors(Color[] hintCandidateAlsColors) {
        this.hintCandidateAlsColors = hintCandidateAlsColors;
    }

    public Color getArrowColor() {
        return arrowColor;
    }

    public void setArrowColor(Color arrowColor) {
        this.arrowColor = arrowColor;
    }

    public double getValueFontFactor() {
        return valueFontFactor;
    }

    public void setValueFontFactor(double valueFontFactor) {
        this.valueFontFactor = valueFontFactor;
    }

    public double getCandidateFontFactor() {
        return candidateFontFactor;
    }

    public void setCandidateFontFactor(double candidateFontFactor) {
        this.candidateFontFactor = candidateFontFactor;
    }

    public double getHintBackFactor() {
        return hintBackFactor;
    }

    public void setHintBackFactor(double hintBackFactor) {
        this.hintBackFactor = hintBackFactor;
    }

    public Font getDefaultValueFont() {
        return defaultValueFont;
    }

    public void setDefaultValueFont(Font defaultValueFont) {
        this.defaultValueFont = defaultValueFont;
    }

    public Font getDefaultCandidateFont() {
        return defaultCandidateFont;
    }

    public void setDefaultCandidateFont(Font defaultCandidateFont) {
        this.defaultCandidateFont = defaultCandidateFont;
    }

    public Font getBigFont() {
        return bigFont;
    }

    public void setBigFont(Font bigFont) {
        this.bigFont = bigFont;
    }

    public Font getSmallFont() {
        return smallFont;
    }

    public void setSmallFont(Font smallFont) {
        this.smallFont = smallFont;
    }

    public DifficultyLevel[] getDifficultyLevels() {
        return difficultyLevels;
    }

    public void setDifficultyLevels(DifficultyLevel[] difficultyLevels) {
        this.difficultyLevels = difficultyLevels;
    }

    public String getDefaultFileDir() {
        return defaultFileDir;
    }

    public void setDefaultFileDir(String defaultFileDir) {
        this.defaultFileDir = defaultFileDir;
    }

    public int getMaxTableEntryLength() {
        return maxTableEntryLength;
    }

    public void setMaxTableEntryLength(int maxTableEntryLength) {
        this.maxTableEntryLength = maxTableEntryLength;
    }

    public int getAnzTableLookAhead() {
        return anzTableLookAhead;
    }

    public void setAnzTableLookAhead(int anzTableLookAhead) {
        this.anzTableLookAhead = anzTableLookAhead;
    }

    public Color[] getColoringColors() {
        return coloringColors;
    }

    public void setColoringColors(Color[] coloringColors1) {
        this.coloringColors = coloringColors1;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLaf() {
        return laf;
    }

    public void setLaf(String laf) {
        this.laf = laf;
    }

    public int getInitialHeight() {
        return initialHeight;
    }

    public void setInitialHeight(int initialHeight) {
        this.initialHeight = initialHeight;
    }

    public int getInitialWidth() {
        return initialWidth;
    }

    public void setInitialWidth(int initialWidth) {
        this.initialWidth = initialWidth;
    }

    public int getInitialVertDividerLoc() {
        return initialVertDividerLoc;
    }

    public void setInitialVertDividerLoc(int initialVertDividerLoc) {
        this.initialVertDividerLoc = initialVertDividerLoc;
    }

    public int getInitialHorzDividerLoc() {
        return initialHorzDividerLoc;
    }

    public void setInitialHorzDividerLoc(int initialHorzDividerLoc) {
        this.initialHorzDividerLoc = initialHorzDividerLoc;
    }

    public int getInitialDisplayMode() {
        return initialDisplayMode;
    }

    public void setInitialDisplayMode(int initialDisplayMode) {
        this.initialDisplayMode = initialDisplayMode;
    }

    public int getInitialXPos() {
        return initialXPos;
    }

    public void setInitialXPos(int initialXPos) {
        this.initialXPos = initialXPos;
    }

    public int getInitialYPos() {
        return initialYPos;
    }

    public void setInitialYPos(int initialYPos) {
        this.initialYPos = initialYPos;
    }

    /**
     * @return the showHintPanel
     */
    public boolean isShowHintPanel() {
        return showHintPanel;
    }

    /**
     * @param showHintPanel the showHintPanel to set
     */
    public void setShowHintPanel(boolean showHintPanel) {
        this.showHintPanel = showHintPanel;
    }

    /**
     * @return the showToolBar
     */
    public boolean isShowToolBar() {
        return showToolBar;
    }

    /**
     * @param showToolBar the showToolBar to set
     */
    public void setShowToolBar(boolean showToolBar) {
        this.showToolBar = showToolBar;
    }

    public boolean isSaveWindowLayout() {
        return saveWindowLayout;
    }

    public void setSaveWindowLayout(boolean saveWindowLayout) {
        this.saveWindowLayout = saveWindowLayout;
    }

    public boolean isUseZeroInsteadOfDot() {
        return useZeroInsteadOfDot;
    }

    public void setUseZeroInsteadOfDot(boolean useZeroInsteadOfDot) {
        this.useZeroInsteadOfDot = useZeroInsteadOfDot;
    }

    public boolean isAllowErsWithOnlyTwoCandidates() {
        return allowErsWithOnlyTwoCandidates;
    }

    public void setAllowErsWithOnlyTwoCandidates(boolean allowErsWithOnlyTwoCandidates) {
        this.allowErsWithOnlyTwoCandidates = allowErsWithOnlyTwoCandidates;
    }

    public int getKrakenMaxFishType() {
        return krakenMaxFishType;
    }

    public void setKrakenMaxFishType(int krakenMaxFishType) {
        this.krakenMaxFishType = krakenMaxFishType;
    }

    public int getMaxKrakenFins() {
        return maxKrakenFins;
    }

    public void setMaxKrakenFins(int maxKrakenFins) {
        this.maxKrakenFins = maxKrakenFins;
    }

    public int getMaxKrakenEndoFins() {
        return maxKrakenEndoFins;
    }

    public void setMaxKrakenEndoFins(int maxKrakenEndoFins) {
        this.maxKrakenEndoFins = maxKrakenEndoFins;
    }

    public int getKrakenMaxFishSize() {
        return krakenMaxFishSize;
    }

    public void setKrakenMaxFishSize(int krakenMaxFishSize) {
        this.krakenMaxFishSize = krakenMaxFishSize;
    }

    public boolean isAllStepsSearchFish() {
        return allStepsSearchFish;
    }

    public void setAllStepsSearchFish(boolean allStepsSearchFish) {
        this.allStepsSearchFish = allStepsSearchFish;
    }

    public int getAllStepsMaxFishType() {
        return allStepsMaxFishType;
    }

    public void setAllStepsMaxFishType(int allStepsMaxFishType) {
        this.allStepsMaxFishType = allStepsMaxFishType;
    }

    public int getAllStepsMinFishSize() {
        return allStepsMinFishSize;
    }

    public void setAllStepsMinFishSize(int allStepsMinFishSize) {
        this.allStepsMinFishSize = allStepsMinFishSize;
    }

    public int getAllStepsMaxFishSize() {
        return allStepsMaxFishSize;
    }

    public void setAllStepsMaxFishSize(int allStepsMaxFishSize) {
        this.allStepsMaxFishSize = allStepsMaxFishSize;
    }

    public int getAllStepsMaxFins() {
        return allStepsMaxFins;
    }

    public void setAllStepsMaxFins(int allStepsMaxFins) {
        this.allStepsMaxFins = allStepsMaxFins;
    }

    public int getAllStepsMaxEndoFins() {
        return allStepsMaxEndoFins;
    }

    public void setAllStepsMaxEndoFins(int allStepsMaxEndoFins) {
        this.allStepsMaxEndoFins = allStepsMaxEndoFins;
    }

    public boolean isAllStepsCheckTemplates() {
        return allStepsCheckTemplates;
    }

    public void setAllStepsCheckTemplates(boolean allStepsCheckTemplates) {
        this.allStepsCheckTemplates = allStepsCheckTemplates;
    }

    public int getAllStepsKrakenMaxFishType() {
        return allStepsKrakenMaxFishType;
    }

    public void setAllStepsKrakenMaxFishType(int allStepsKrakenMaxFishType) {
        this.allStepsKrakenMaxFishType = allStepsKrakenMaxFishType;
    }

    public int getAllStepsKrakenMinFishSize() {
        return allStepsKrakenMinFishSize;
    }

    public void setAllStepsKrakenMinFishSize(int allStepsKrakenMinFishSize) {
        this.allStepsKrakenMinFishSize = allStepsKrakenMinFishSize;
    }

    public int getAllStepsKrakenMaxFishSize() {
        return allStepsKrakenMaxFishSize;
    }

    public void setAllStepsKrakenMaxFishSize(int allStepsKrakenMaxFishSize) {
        this.allStepsKrakenMaxFishSize = allStepsKrakenMaxFishSize;
    }

    public int getAllStepsMaxKrakenFins() {
        return allStepsMaxKrakenFins;
    }

    public void setAllStepsMaxKrakenFins(int allStepsMaxKrakenFins) {
        this.allStepsMaxKrakenFins = allStepsMaxKrakenFins;
    }

    public int getAllStepsMaxKrakenEndoFins() {
        return allStepsMaxKrakenEndoFins;
    }

    public void setAllStepsMaxKrakenEndoFins(int allStepsMaxKrakenEndoFins) {
        this.allStepsMaxKrakenEndoFins = allStepsMaxKrakenEndoFins;
    }

    public boolean isAllowDualsAndSiamese() {
        return allowDualsAndSiamese;
    }

    public void setAllowDualsAndSiamese(boolean allowDualsAndSiamese) {
        this.allowDualsAndSiamese = allowDualsAndSiamese;
    }

    public boolean isOnlyOneFishPerStep() {
        return onlyOneFishPerStep;
    }

    public void setOnlyOneFishPerStep(boolean onlyOneFishPerStep) {
        this.onlyOneFishPerStep = onlyOneFishPerStep;
    }

    public boolean isOnlyOneAlsPerStep() {
        return onlyOneAlsPerStep;
    }

    public void setOnlyOneAlsPerStep(boolean onlyOneAlsPerStep) {
        this.onlyOneAlsPerStep = onlyOneAlsPerStep;
    }

    public boolean isAllowAlsOverlap() {
        return allowAlsOverlap;
    }

    public void setAllowAlsOverlap(boolean allowAlsOverlap) {
        this.allowAlsOverlap = allowAlsOverlap;
    }

    public boolean isAllStepsOnlyOneAlsPerStep() {
        return allStepsOnlyOneAlsPerStep;
    }

    public void setAllStepsOnlyOneAlsPerStep(boolean allStepsOnlyOneAlsPerStep) {
        this.allStepsOnlyOneAlsPerStep = allStepsOnlyOneAlsPerStep;
    }

    public boolean isAllStepsAllowAlsOverlap() {
        return allStepsAllowAlsOverlap;
    }

    public void setAllStepsAllowAlsOverlap(boolean allStepsAllowAlsOverlap) {
        this.allStepsAllowAlsOverlap = allStepsAllowAlsOverlap;
    }

    public String getAllStepsFishCandidates() {
        return allStepsFishCandidates;
    }

    public void setAllStepsFishCandidates(String allStepsFishCandidates) {
        this.allStepsFishCandidates = allStepsFishCandidates;
    }

    public String getAllStepsKrakenFishCandidates() {
        return allStepsKrakenFishCandidates;
    }

    public void setAllStepsKrakenFishCandidates(String allStepsKrakenFishCandidates) {
        this.allStepsKrakenFishCandidates = allStepsKrakenFishCandidates;
    }

    public boolean isOnlyOneChainPerStep() {
        return onlyOneChainPerStep;
    }

    public void setOnlyOneChainPerStep(boolean onlyOneChainPerStep) {
        this.onlyOneChainPerStep = onlyOneChainPerStep;
    }

    public boolean isAllowAlsInTablingChains() {
        return allowAlsInTablingChains;
    }

    public void setAllowAlsInTablingChains(boolean allowAlsInTablingChains) {
        this.allowAlsInTablingChains = allowAlsInTablingChains;
    }

    public boolean isAllStepsAllowAlsInTablingChains() {
        return allStepsAllowAlsInTablingChains;
    }

    public void setAllStepsAllowAlsInTablingChains(boolean allStepsAllowAlsInTablingChains) {
        this.allStepsAllowAlsInTablingChains = allStepsAllowAlsInTablingChains;
    }

    /**
     * @return the historySize
     */
    public int getHistorySize() {
        return historySize;
    }

    /**
     * @param aHistorySize the historySize to set
     */
    public void setHistorySize(int aHistorySize) {
        historySize = aHistorySize;
    }
}
