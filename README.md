# BrailleBlaster-NG

BrailleBlaster-NG is a community enhanced fork of the BrailleBlaster software. As a community project it aims to focus on and respond to the needs of the community rather than being driven by the requirements of a single business.

BrailleBlaster is a free, open-source Braille transcription program developed by the American Printing House for the Blind (APH). It assists individuals and professional transcribers in producing high-quality Braille materials, ensuring that individuals who are blind have timely access to essential reading resources.

## Obtaining BrailleBlaster-NG

At the moment no production ready release has been made of BrailleBlaster-NG. This section will be updated with details of a production release when it is made. In the meantime users who want such a release should consider using the [previous proprietary version of BrailleBlaster](https://brailleblaster.org/download.php).

Users who want to be on the bleeding edge and are prepared to take the risks of using development builds may wish to try out the [continuous release](https://github.com/mwhapples/brailleblaster-ng/releases/continuous). See the below details for running a development build.

## Building BrailleBlaster-NG

BrailleBlaster-NG uses the maven build system and requires a (java Development Kit of Java17 or higher to be installed. You do not need to have maven installed as BrailleBlaster-NG includes some maven wrapper scripts which will be able to download the required version of maven. To build BrailleBlaster-NG run the following command at the root of the source tree:
```command line
mvnw package
```
Once the build finishes you will find the application in brailleblaster-app/target/dist.

## Running a development build

To run a development build of BrailleBlaster-NG, either one you built yourself or from the continuous release, you will need (java17 or higher installed. On Windows or Linux issue the following command from the root of your build:
```command line
java -jar brailleblaster.jar
```
Mac users will need a different command which is:
```bash
java -XstartOnFirstThread -jar brailleblaster.jar
```
