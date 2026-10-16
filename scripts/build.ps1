param([switch]$Test, [string[]]$TestClass)
$ErrorActionPreference = 'Stop'
. (Join-Path $PSScriptRoot 'java-tools.ps1')
$projectRoot = Split-Path $PSScriptRoot -Parent
$classes = Join-Path $projectRoot 'build\classes'
$testClasses = Join-Path $projectRoot 'build\test-classes'
New-Item -ItemType Directory -Path $classes,$testClasses -Force | Out-Null
$sources = @(Get-ChildItem -LiteralPath (Join-Path $projectRoot 'src') -Filter '*.java' -Recurse |
    ForEach-Object { $_.FullName })
& (Get-JavaTool 'javac') -encoding UTF-8 --release 8 -Xlint:all -Werror -d $classes @sources
if ($LASTEXITCODE -ne 0) { throw 'Application compilation failed.' }
Write-Host 'Application compilation passed (Java 8 target, strict warnings).'
if ($Test) {
    $libraries = @(Get-ChildItem -LiteralPath (Join-Path $projectRoot 'lib') -Filter '*.jar' |
        ForEach-Object { $_.FullName })
    $classpath = (@($classes,$testClasses) + $libraries) -join ';'
    $testSources = @(Get-ChildItem -LiteralPath (Join-Path $projectRoot 'test') -Filter '*.java' -Recurse)
    & (Get-JavaTool 'javac') -encoding UTF-8 --release 8 -Xlint:all -Werror -cp $classpath -d $testClasses @($testSources.FullName)
    if ($LASTEXITCODE -ne 0) { throw 'JUnit test compilation failed.' }
    if (-not $TestClass) {
        $TestClass = @($testSources | Where-Object { $_.Name -like '*Test.java' } |
            ForEach-Object { "UNO." + $_.BaseName })
    }
    if (-not $TestClass) { throw 'No JUnit test classes were found.' }
    $TestClass = @($TestClass | ForEach-Object {
        if ($_ -like '*.*') { $_ } else { 'UNO.' + $_ }
    })
    & (Get-JavaTool 'java') -cp $classpath org.junit.runner.JUnitCore @TestClass
    if ($LASTEXITCODE -ne 0) { throw 'JUnit tests failed.' }
}
