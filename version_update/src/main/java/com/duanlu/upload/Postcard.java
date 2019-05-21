package com.duanlu.upload;

/********************************
 * @name Postcard
 * @author 段露
 * @createDate 2019/05/20 12:02
 * @updateDate 2019/05/20 12:02
 * @version V1.0.0
 * @describe 信息.
 ********************************/
public class Postcard {

    public String downloadUrl;
    public boolean isForce;//是否强制更新.

    public String saveDir;
    public String saveName;

    private Postcard() {

    }

    public static class Builder {
        private String downloadUrl;
        private boolean isForce;//是否强制更新.

        private String saveDir;
        private String saveName;

        public Builder(String downloadUrl) {
            this.downloadUrl = downloadUrl;
        }

        public Builder setSaveConfig(String saveDir, String saveName) {
            this.saveDir = saveDir;
            this.saveName = saveName;
            return this;
        }

        public Builder setForce(boolean force) {
            isForce = force;
            return this;
        }

        public Postcard build() {
            Postcard postcard = new Postcard();
            postcard.downloadUrl = this.downloadUrl;
            postcard.isForce = this.isForce;//是否强制更新.

            postcard.saveDir = this.saveDir;
            postcard.saveName = this.saveName;
            return postcard;
        }
    }

    @Override
    public String toString() {
        return "Postcard{" +
                "downloadUrl='" + downloadUrl + '\'' +
                ", isForce=" + isForce +
                ", saveDir='" + saveDir + '\'' +
                ", saveName='" + saveName + '\'' +
                '}';
    }
}
