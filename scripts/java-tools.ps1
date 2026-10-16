function Get-JavaTool([string]$Name) {
    $candidates = @()
    if ($env:JAVA_HOME) { $candidates += $env:JAVA_HOME }
    $javaDirectory = Join-Path $env:ProgramFiles 'Java'
    if (Test-Path -LiteralPath $javaDirectory) {
        $candidates += @(Get-ChildItem -LiteralPath $javaDirectory -Directory |
            Where-Object { $_.Name -like 'jdk*' } |
            Sort-Object Name | ForEach-Object { $_.FullName })
    }
    foreach ($candidate in $candidates) {
        $tool = Join-Path $candidate "bin\$Name.exe"
        if (Test-Path -LiteralPath $tool) { return $tool }
    }
    $command = Get-Command "$Name.exe" -ErrorAction SilentlyContinue
    if ($command) { return $command.Source }
    throw 'Install a JDK (11 or newer) and set JAVA_HOME to its directory.'
}
