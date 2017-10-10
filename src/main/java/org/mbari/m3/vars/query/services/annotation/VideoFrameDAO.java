package org.mbari.m3.vars.query.services.annotation;

import java.util.Set;
import org.mbari.m3.vars.query.services.DAO;

/**
 * Created by IntelliJ IDEA.
 * User: brian
 * Date: Aug 7, 2009
 * Time: 2:57:21 PM
 * To change this template use File | Settings | File Templates.
 */
public interface VideoFrameDAO extends DAO {

    Set<VideoFrame> findAllByVideoArchivePrimaryKey(Object primaryKey);

    VideoFrame findByPrimaryKey(Object primaryKey);

    VideoFrame findByTimeCodeAndVideoArchiveName(String timecode, String videoArchiveName);

}
