package org.mbari.m3.vars.query;

import org.mbari.m3.vars.query.model.ILink;
import org.mbari.m3.vars.query.model.LinkBean;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * @author Brian Schlining
 * @since 2019-08-20T15:06:00
 */
public class Constants {

    public static final String WILD_CARD = "*";
    public static final ILink WILD_CARD_LINK = new LinkBean(WILD_CARD, WILD_CARD, WILD_CARD);
    public static final String TOPIC_NONFATAL_ERROR = "vars.shared.ui.GlobalLookup-TopicNonfatalError";

    /**
     * Subscribers to this topic will get and {@link Exception} as the data
     */
    public static final String TOPIC_FATAL_ERROR = "vars.shared.ui.GlobalLookup-TopicFatalError";

    public static final String TOPIC_WARNING = "vars.shared.ui.GlobalLookup-TopicWarning";

    public static final String TOPIC_USERACCOUNT = "vars.shared.ui.GlobalLookup-UserAccount";

    public static final String TOPIC_EXIT = "vars.shared.ui.GlobalLookup-Exit";

    public static DateFormat newUTCDateFormat() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat;
    }
}
