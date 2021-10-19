### To build HoDoKu Dark mode version
- The **com** folder and contents must be added to the **src** folder
- The **img** images therein must replace those in the **src/img** folder
- MainFrame.java and Options.java must be modified as per patches
- Build Hodoku-Dark.jar
#### HoDoKu Dark mode
- _com/formdev/flatlaf_ - FlatLaf 0.42, from: https://www.formdev.com/flatlaf/
- _img_ - these images are as provided by @CCV54, see issue #6
- _MainFrame.patch_ - for MainFrame.java changes for Dark mode
- _Options.patch_ - for Options.java color changes for Dark mode
#### Patch files
- The patch files were created comparing HoDoKu 2.2.3 files
- They were created using: diff (GNU diffutils) 3.7
