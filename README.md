# BrailleBlaster-NG

BrailleBlaster-NG is a community enhanced fork of the BrailleBlaster software. As a community project it aims to focus on and respond to the needs of the community rather than being driven by the requirements of a single organisation.

BrailleBlaster is a free, open-source Braille transcription program developed by the American Printing House for the Blind (APH). It assists individuals and professional transcribers in producing high-quality Braille materials, ensuring that individuals who are blind have timely access to essential reading resources.

## Obtaining BrailleBlaster-NG

The latest release of BrailleBlaster-NG can be downloaded from the [download page](https://download.brailleblaster-ng.app/download.html). Older releases are archived on the project's [GitHub Releases page](https://github.com/mwhapples/BrailleBlaster-NG/releases).

Users who want to be on the bleeding edge and are prepared to take the risks of using development builds may wish to try out the [continuous release](https://github.com/mwhapples/brailleblaster-ng/releases/continuous). See the below details for running a development build.

## Documentation

Online and downloadable copies of the documentation can be found on [the documentation pages](https://docs.brailleblaster-ng.app). Those wanting the source and wishing to contribute to the documentation of BrailleBlaster-NG, should visit the [documentation GitHub repository](https://github.com/mwhapples/BrailleBlaster-NG-docs).

## Asking questions and reporting issues

At the moment users are encouraged to use [GitHub Discussions](https://github.com/mwhapples/BrailleBlaster-NG/discussions) for asking questions, sharing ideas and seeking help with issues with the software. Issue tracking will be done using [GitHub Issues](https://github.com/mwhapples/BrailleBlaster-NG/issues).

## Contributing to BrailleBlaster-NG

As a community based project, any contributions from the community would be welcome. These may be code contributions, documentation or website, or any other area you feel you can contribute. Please submit contributions in pull requests.

BrailleBlaster-NG at the moment is trying to remain compatible with the upstream BrailleBlaster project from APH, however this is not guaranteed to remain the case in the future. So if you would like your contribution to be passed upstream to APH for inclusion into BrailleBlaster as well as being used in BrailleBlaster-NG, it is recommended you base your pull request on the aph branch. If you want your contribution to only be included in BrailleBlaster-NG then base your pull request on the main branch of the BrailleBlaster-NG repository.

## Building BrailleBlaster-NG

BrailleBlaster-NG uses the maven build system and requires a Java Development Kit of Java21 or higher to be installed. You do not need to have maven installed as BrailleBlaster-NG includes some maven wrapper scripts which will be able to download the required version of maven. To build BrailleBlaster-NG run the following command at the root of the source tree:
```console
mvnw package
```
Once the build finishes you will find the application in brailleblaster-app/target/dist.

## Running a development build

To run a development build of BrailleBlaster-NG, either one you built yourself or from the continuous release, you will need Java21 or higher installed. On Windows or Linux issue the following command from the root of your build:
```console
java -jar brailleblaster.jar
```
Mac users will need a different command which is:
```bash
java -XstartOnFirstThread -jar brailleblaster.jar
```
