$echo off
set project_path=%~dp0
set build_path=%project_path%build\\
set flags=""

echo build_path=%build_path%
echo project_path=%project_path%

mkdir %build_path%
cmake.exe -G "Visual Studio 15 2017 Win64" -T host=x64 -B %build_path%  -H "%project_path%"
cmake.exe --open %build_path%
