rootProject.name = "jaybird-firebird-embedded"

include("win32-x86-64")
project(":win32-x86-64").name = "jaybird-firebird-embedded-win32-x86-64"

includeBuild("build-logic")
