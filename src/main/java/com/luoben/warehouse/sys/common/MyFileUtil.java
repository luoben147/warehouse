package com.luoben.warehouse.sys.common;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Properties;

public class MyFileUtil {

    /**
     * 文件上传的保存路径,给个默认值
     * 默认当前项目目录 D:\\worker\\IDEAWork\\warehouse\\upload\\
     */
    public static String UPLOAD_PATH = System.getProperty("user.dir") + File.separator + "upload" + File.separator;

    /**
     * 图片保存的公共路径
     * D:\\worker\\IDEAWork\\warehouse\\upload\\
     */
    public static String IMAGE_DIRECTORY = "file:" + UPLOAD_PATH;


    /**
     * 读取yml里的属性值
     */
    static {
        YamlPropertiesFactoryBean yamlMapFactoryBean = new YamlPropertiesFactoryBean();
        //可以加载多个yml文件
        yamlMapFactoryBean.setResources(new ClassPathResource("application.yml"));
        Properties properties = yamlMapFactoryBean.getObject();
        //获取yml里的参数
        String filepath = properties.getProperty("filepath");
        if(null!=filepath){
            UPLOAD_PATH =filepath;
        }
    }

    /**
     * 根据文件老名字得到新名字
     *
     * @param oldName
     * @return
     */
    public static String createNewFileName(String oldName) {
        String suffix = FileUtil.extName(oldName);
        return IdUtil.fastSimpleUUID() + "." + suffix;
    }

    /**
     * 保存文件,返回图片所在目录和图片自己
     *
     * @return 如2020-02-12/54255afd988f47648cd8a6675ae8c55c.jpg
     */
    public static String save(FileInputStream fis, String fileName) {
        FileOutputStream fos = null;
        //创建通道
        FileChannel inChannel = null;
        FileChannel outChannel = null;
        //要返回的路径
        String selfPath = null;
        //图片公用的存储位置
        String publicPath = null;

        System.out.println("文件上传路径= "+MyFileUtil.UPLOAD_PATH);

        try {
            selfPath = DateUtil.today() + "/" + fileName;
            publicPath = MyFileUtil.UPLOAD_PATH + selfPath;
            //如不存在则创建目录及文件
            FileUtil.touch(publicPath);
            fos = new FileOutputStream(publicPath);

            inChannel = fis.getChannel();
            outChannel = fos.getChannel();
            //通道间传输
            inChannel.transferTo(0, inChannel.size(), outChannel);

            inChannel.close();
            outChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return selfPath;
    }


    /**
     * 文件下载
     * @param path
     * @return
     */
    public static ResponseEntity<Object> createResponseEntity(String path) {
        //1,构造文件对象
        File file=new File(UPLOAD_PATH, path);
        if(file.exists()) {
            //将下载的文件，封装byte[]
            byte[] bytes=null;
            try {
                bytes =FileUtil.readBytes(file);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //创建封装响应头信息的对象
            HttpHeaders header=new HttpHeaders();
            //封装响应内容类型(APPLICATION_OCTET_STREAM 响应的内容不限定)
            header.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            //设置下载的文件的名称
//			header.setContentDispositionFormData("attachment", "123.jpg");
            //创建ResponseEntity对象
            ResponseEntity<Object> entity=
                    new ResponseEntity<Object>(bytes, header, HttpStatus.CREATED);
            return entity;
        }
        return null;
    }


    /**
     * 根据路径改名字 去掉_temp
     * @param goodsimg
     * @return
     */
    public static String renameFile(String goodsimg) {
        File file=new File(UPLOAD_PATH, goodsimg);
        String replace = goodsimg.replace("_temp", "");
        if(file.exists()) {
            file.renameTo(new File(UPLOAD_PATH, replace));
        }
        return replace;
    }

    /**
     * 根据老路径删除图片
     * @param oldPath
     */
    public static void removeFileByPath(String oldPath) {
        if(!oldPath.equals(Constast.IMAGES_DEFAULTGOODSIMG_PNG)) {
            File file=new File(UPLOAD_PATH, oldPath);
            if(file.exists()) {
                file.delete();
            }
        }
    }

}



