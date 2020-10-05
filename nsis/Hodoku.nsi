;HoDoKu Installer Script

  Unicode True

!define WELCOME_TEXT \
"Setup will guide you through the installation of $(^NameDA) Version 2.2.2 (Build 133).$\r$\n$\r$\n\
It is recommended that you close all other applications before starting Setup. \
This will make it possible to update relevant system files without having to reboot your computer.$\r$\n$\r$\n\
IMPORTANT: If you have installed a previous $(^NameDA) (downloaded from sourceforge.net), \
please uninstall it first before clicking Next.$\r$\n$\r$\n\
$_CLICK"

;Include Modern UI

  !include "MUI2.nsh"

;General

  ;Name and file
  Name "HoDoKu"
  OutFile "Hodoku-Installer.exe"

  ;Default installation folder
  InstallDir "$ProgramFiles\HoDoKu"

  ;Get installation folder from registry if available
  InstallDirRegKey HKCU "Software\HoDoKu" ""

  ;Request application privileges for Windows Vista
  RequestExecutionLevel admin

;Variables

  Var StartMenuFolder

;Interface Settings

  !define MUI_ABORTWARNING

;Pages

  !define MUI_WELCOMEPAGE_TEXT '${WELCOME_TEXT}'
  !insertmacro MUI_PAGE_WELCOME
  !insertmacro MUI_PAGE_LICENSE "COPYING.txt"
  !insertmacro MUI_PAGE_DIRECTORY

  ;Start Menu Folder Page Configuration
  !define MUI_STARTMENUPAGE_REGISTRY_ROOT "HKCU"
  !define MUI_STARTMENUPAGE_REGISTRY_KEY "Software\HoDoKu"
  !define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "Start Menu Folder"
  !insertmacro MUI_PAGE_STARTMENU Application $StartMenuFolder
  !insertmacro MUI_PAGE_INSTFILES
  !define MUI_FINISHPAGE_RUN "$INSTDIR\Hodoku.exe"
  !insertmacro MUI_PAGE_FINISH

  !insertmacro MUI_UNPAGE_WELCOME
  !insertmacro MUI_UNPAGE_CONFIRM
  !insertmacro MUI_UNPAGE_INSTFILES
  !insertmacro MUI_UNPAGE_FINISH

;Languages

  !insertmacro MUI_LANGUAGE "English"

;Installer Sections

Section "Hodoku" Hodoku
  SetShellVarContext current

  SetOutPath "$INSTDIR"

  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application

  ;Store installation folder
  WriteRegStr HKCU "Software\HoDoKu" "" $INSTDIR

  File COPYING.txt
  File Hodoku.exe

  CreateShortCut  "$DESKTOP\HoDoKu.lnk" "$INSTDIR\Hodoku.exe"

  ;Create shortcuts
  CreateDirectory "$SMPROGRAMS\$StartMenuFolder"
  CreateShortCut  "$SMPROGRAMS\$StartMenuFolder\Uninstall.lnk" "$INSTDIR\Uninstall.exe"
  CreateShortCut  "$SMPROGRAMS\$StartMenuFolder\Licence.lnk"   "$INSTDIR\COPYING.txt"
  CreateShortCut  "$SMPROGRAMS\$StartMenuFolder\HoDoKu.lnk"    "$INSTDIR\Hodoku.exe"
  CreateShortCut  "$SMPROGRAMS\$StartMenuFolder\Solving Techniques (German).lnk"  "http://hodoku.sourceforge.net/de/techniques.php"
  CreateShortCut  "$SMPROGRAMS\$StartMenuFolder\Solving Techniques (English).lnk" "http://hodoku.sourceforge.net/en/techniques.php"
  CreateShortCut  "$SMPROGRAMS\$StartMenuFolder\User Manual (German).lnk"  "http://hodoku.sourceforge.net/de/docs.php"
  CreateShortCut  "$SMPROGRAMS\$StartMenuFolder\User Manual (English).lnk" "http://hodoku.sourceforge.net/en/docs.php"

  ;Create uninstaller
  WriteUninstaller "$INSTDIR\Uninstall.exe"

  !insertmacro MUI_STARTMENU_WRITE_END

SectionEnd

;Uninstaller Section

Section "Uninstall"
  SetShellVarContext current

  !insertmacro MUI_STARTMENU_GETFOLDER Application $StartMenuFolder

  ; Remove shortcuts
  Delete "$SMPROGRAMS\$StartMenuFolder\User Manual (English).lnk"
  Delete "$SMPROGRAMS\$StartMenuFolder\User Manual (German).lnk"
  Delete "$SMPROGRAMS\$StartMenuFolder\Solving Techniques (English).lnk"
  Delete "$SMPROGRAMS\$StartMenuFolder\Solving Techniques (German).lnk"
  Delete "$SMPROGRAMS\$StartMenuFolder\HoDoKu.lnk"
  Delete "$SMPROGRAMS\$StartMenuFolder\Licence.lnk"
  Delete "$SMPROGRAMS\$StartMenuFolder\Uninstall.lnk"
  RMDir  "$SMPROGRAMS\$StartMenuFolder"

  Delete "$DESKTOP\HoDoKu.lnk"

  Delete "$INSTDIR\Hodoku.exe"
  Delete "$INSTDIR\COPYING.txt"
  Delete "$INSTDIR\Uninstall.exe"
  RMDir  "$INSTDIR"

  DeleteRegKey /ifempty HKCU "Software\HoDoKu"

SectionEnd
