$ErrorActionPreference = 'Stop'
& (Join-Path $PSScriptRoot 'build.ps1')
. (Join-Path $PSScriptRoot 'java-tools.ps1')
$projectRoot = Split-Path $PSScriptRoot -Parent
& (Get-JavaTool 'java') -cp (Join-Path $projectRoot 'build\classes') UNO.UnoGame
if ($LASTEXITCODE -ne 0) { throw 'The program failed.' }
