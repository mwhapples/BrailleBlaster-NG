package org.brailleblaster.perspectives.mvc.events

import org.brailleblaster.perspectives.braille.messages.Sender
import org.brailleblaster.perspectives.mvc.SimpleEvent

class AppStartedEvent(sender: Sender) : SimpleEvent(sender)