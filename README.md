### HoDoKu

This HoDoKu is my modifications to hobiwan's 2.2.0 Hodoku.jar [HoDoKu - v2.2.0 (Build 116)] released 2012-07-29.

### Release notes

HoDoKu is a solver/generator/trainer/analyzer for standard sudoku. It is written in Java/Swing and should therefore run on any platform supported by Java (tested on Windows and Linux - Ubuntu/GTK+-LAF). Since it is written in Java the Java Runtime Environment (JRE) version 1.6 or higher must be installed on your computer before you can run HoDoKu. The JRE can be downloaded from http://www.oracle.com/technetwork/java/javase/downloads/index.html

Available languages: English and German

For all Windows versions hodoku.exe is the preferred program version. For all other operating systems hodoku.jar has to be used. HoDoKu uses rather a lot of memory (especially if you use the "Find all available steps" feature). The recommended way to run HoDoKu is:
```
java -Xmx512m -jar hodoku.jar
```
Note: the parameter "-Xmx" is specific to the JRE provided by Oracle. If you use a different JRE, please look up the correct parameter for setting the maximum heap size at startup.

*Source*: hobiwan's Release notes, with 1 typo fixed, and other small changes!

### hobiwan's Sudoku for Java - HoDoKu Web Site
```
http://hodoku.sourceforge.net/
```
### Change log

#### Version 2.2.4 (Build 150) [2022-01-05]

- add help option to get offline Eng docs
- Fix Save Puzzle Error (for installed Hodoku), #13
- hodoku.hcfg corrupted Config Bug, @PseudoFish fix, #15
- Solve up to Bug, @PseudoFish fix, #12
- Infinite Loop Bug, @PseudoFish fix, #11

#### Version 2.2.3 (Build 144) [2021-10-19]

- Dark Mode stuff, #6
- updates to colors and images from @CCV54, #9
- add 'Overwrite Sudoku' dialog when loading sudoku from file or history, #4
- center (most) dialogs relative to Hodoku window, #5
- default folder changed from user Home folder to Hodoku folder
- add hint to message when next Hint/Step not available
- some typo fixes
- improvements to Copy for SimpleSudoku output
- remove forms from src/sudoku folder
- add uninstall thru windows control panel (w10 settings)

#### Version 2.2.2 (Build 133) [2020-10-05]

- updates to colors and images from @CCV54
- how to create the Hodoku Windows Installer
- changes to Help menu items
- how to wrap Hodoku.jar into a Windows exe
- center confirm dialogs on parent window instead of desktop
- fix 225 Extended print: wrong colors
- typo fix in SolutionPanel.java

#### Version 2.2.1 (Build 125) [2020-09-28]

This release is equivalent to @CCV54's HoDoKu_221.jar

- ask to 'Confirm' when 'Create a new Sudoku.'
- fix hint for three candidates XYZ-Wing
- add ColorKu inactive image filter
- use default colors from @CCV54's HoDoKu_221.jar
- fix typo in keyboard.html
- use images from @CCV54's HoDoKu_221.jar
- fix buttons 3/4/5 selection from ColorKu color panel
- fix typo in HintToolButton.toolTipText

#### Version 2.2.0 (2020-09-21)

This release is equivalent to hobiwan's 2.2.0 Hodoku.jar [HoDoKu - v2.2.0 (Build 116)] released 2012-07-29

#### Version 2.2 (2012-07-31)

For hobiwan's **Release notes**, see [release-2.2.txt](https://github.com/1to9only/HoDoKu/blob/master/release-2.2.txt)

####
