package com.been;

import java.util.List;

/**
 * Created by xushuzhan on 2017/3/23.
 */

public class HeathyTipsBeen {


    /**
     * status : 200
     * message : ok
     * data : [{"Id":15,"Name":"减压 | 下班必做的9个瑜伽动作！ ","Author":"微信公众号\u201c瑜伽网\u201d","Resume":"再没有什么比在办公室坐一整天更能让你腰酸背痛脑袋木了。幸运的是，我们有瑜伽可以帮助我们释放压力。瑜伽不仅仅可以解决身体上的不适，还能释放一天中累积的精神压力。"},{"Id":16,"Name":"科普：几种食物能够带来快乐，帮你减压减压","Author":"资料来源于网络","Resume":"食物可以带给人的东西很多，绝对超过你的想象。开心和快乐的感受可以从食物中获取，忧郁和伤心也可以从食物中获得。所以为了自己的心情还是好好管住自己的嘴巴吧！"},{"Id":17,"Name":"你知道吗？家居这样布局，可以减压！","Author":"新浪家居","Resume":"减压从家居的环境着手，打造欢快轻松明亮的环境，在视觉效果上为自己工作减压。下面小编就教你利用一些家居摆设来帮助减轻压力，效果很神奇哦，赶紧来试试吧!"}]
     */

    private int status;
    private String message;
    private List<DataBean> data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * Id : 15
         * Name : 减压 | 下班必做的9个瑜伽动作！
         * Author : 微信公众号“瑜伽网”
         * Resume : 再没有什么比在办公室坐一整天更能让你腰酸背痛脑袋木了。幸运的是，我们有瑜伽可以帮助我们释放压力。瑜伽不仅仅可以解决身体上的不适，还能释放一天中累积的精神压力。
         */

        private int Id;
        private String Name;
        private String Author;
        private String Resume;

        public int getId() {
            return Id;
        }

        public void setId(int Id) {
            this.Id = Id;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getAuthor() {
            return Author;
        }

        public void setAuthor(String Author) {
            this.Author = Author;
        }

        public String getResume() {
            return Resume;
        }

        public void setResume(String Resume) {
            this.Resume = Resume;
        }
    }
}
