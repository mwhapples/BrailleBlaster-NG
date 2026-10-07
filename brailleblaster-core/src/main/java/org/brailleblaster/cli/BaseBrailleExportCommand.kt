package org.brailleblaster.cli

import picocli.CommandLine

abstract class BaseBrailleExportCommand {
    @CommandLine.Option(names = ["--braille-profile"], description = ["Braille profile to use for this conversion"], paramLabel = "<profile>")
    var brailleProfile: String? = null
}