package com.fun.entity;

import lombok.Data;

import java.util.Date;

@Data
public class HeadLine {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_head_line.line_id
     *
     * @mbggenerated Sun Nov 25 19:26:11 CST 2018
     */
    private Integer lineId;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_head_line.line_name
     *
     * @mbggenerated Sun Nov 25 19:26:11 CST 2018
     */
    private String lineName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_head_line.line_link
     *
     * @mbggenerated Sun Nov 25 19:26:11 CST 2018
     */
    private String lineLink;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_head_line.line_img
     *
     * @mbggenerated Sun Nov 25 19:26:11 CST 2018
     */
    private String lineImg;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_head_line.priority
     *
     * @mbggenerated Sun Nov 25 19:26:11 CST 2018
     */
    private Integer priority;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_head_line.enable_status
     *
     * @mbggenerated Sun Nov 25 19:26:11 CST 2018
     */
    private Integer enableStatus;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_head_line.create_time
     *
     * @mbggenerated Sun Nov 25 19:26:11 CST 2018
     */
    private Date createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column tb_head_line.last_edit_time
     *
     * @mbggenerated Sun Nov 25 19:26:11 CST 2018
     */
    private Date lastEditTime;

}