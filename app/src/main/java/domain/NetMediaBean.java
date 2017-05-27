package domain;

import java.io.Serializable;

/**
 * Created by Administrator on 2017/5/11.
 */

public class NetMediaBean implements Serializable{
    private int id; //影片id
    private String movieName;//电影名称
    private String coverImg;//图片url
    private int movieID;
    private String playUrl;//播放地址
    private String highPlayUrl;//高清播放地址
    private String videoTitle;//标题
    private int videoLength;//时长
    private String summary;//简介


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMovieName() {
        return this.movieName;
    }

    public void setMovieName(String movieName) {
        this.movieName = movieName;
    }

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public int getMovieID() {
        return movieID;
    }

    public void setMovieID(int movieID) {
        this.movieID = movieID;
    }

    public String getPlayUrl() {
        return playUrl;
    }

    public void setPlayUrl(String playUrl) {
        this.playUrl = playUrl;
    }

    public String getHighPlayUrl() {
        return highPlayUrl;
    }

    public void setHighPlayUrl(String highPlayUrl) {
        this.highPlayUrl = highPlayUrl;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public int getVideoLength() {
        return videoLength;
    }

    public void setVideoLength(int videoLength) {
        this.videoLength = videoLength;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "NetMediaBean{" +
                "id=" + id +
                ", movieName='" + movieName + '\'' +
                ", coverImg='" + coverImg + '\'' +
                ", movieID=" + movieID +
                ", playUrl='" + playUrl + '\'' +
                ", highPlayUrl='" + highPlayUrl + '\'' +
                ", videoTitle='" + videoTitle + '\'' +
                ", videoLength=" + videoLength +
                ", summary='" + summary + '\'' +
                '}';
    }
}
