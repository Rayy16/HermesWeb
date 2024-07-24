create table user_info (
                           id bigint not null auto_increment comment '自增主键',
                           uid char(36) not null comment '用户唯一标识',
                           user_name varchar(255) not null comment '用户名称',
                           email_account varchar(255) not null unique comment '用户邮箱',
                           password varchar(255) not null comment '加密后的用户密码',
                           double_check boolean default false comment '登录是否需要二次验证',
                           icon_link varchar(255) not null default '' comment '用户头像连接',
                           user_role int not null comment '用户角色, 枚举值: 1 - super manager; 2 - inner_member; 3 - plain user',
                           is_deleted boolean default false comment '软删除标识',
                           create_time datetime not null default current_timestamp comment '创建时间',
                           update_time datetime not null default current_timestamp comment '修改时间',
                           delete_time datetime default null comment '删除时间',
                           primary key (id),
                           index idx_user_role_is_deleted (user_role, is_deleted)
) engine = InnoDB default charset = utf8mb4 comment = '存储用户信息的数据表';

create table org_profile_info (
                                  id bigint not null auto_increment comment '自增主键, 组织唯一标识',
                                  org_name varchar(64) not null comment '组织名称',
                                  org_describe varchar(255) not null default '' comment '组织描述',
                                  is_deleted boolean default false comment '软删除标识',
                                  create_time datetime not null default current_timestamp comment '创建时间',
                                  update_time datetime not null default current_timestamp comment '修改时间',
                                  delete_time datetime default null comment '删除时间',
                                  primary key (id)
) engine = InnoDB default charset = utf8mb4 comment '存储组织信息的数据表';

create table org_user_info (
                               id bigint not null auto_increment comment '自增主键',
                               uid char(36) not null comment '用户唯一标识',
                               org_id bigint not null comment '组织唯一标识',
                               org_role int not null comment '用户在组织中的角色, 枚举值: 1 - org_manager; 2 - org_member',
                               create_time datetime not null default current_timestamp comment '创建时间',
                               update_time datetime not null default current_timestamp comment '修改时间',
                               primary key (id),
                               unique key uni_uid_org_id (uid, org_id)
) engine = InnoDB default charset = utf8mb4 comment = '存储用户关联组织的信息的数据表';

create table mail_send_config (
                                  id bigint not null auto_increment comment '自增主键',
                                  template_type varchar(32) unique not null comment '邮件模板的唯一标识',
                                  template_subject varchar(64) not null default '' comment '邮件标题',
                                  template_content varchar(2048) not null comment '邮件模板内容',
                                  is_deleted boolean default false comment '软删除标识',
                                  create_time datetime not null default current_timestamp comment '创建时间',
                                  update_time datetime not null default current_timestamp comment '修改时间',
                                  delete_time datetime default null comment '删除时间',
                                  primary key (id)
) engine = InnoDB default charset = utf8mb4 comment = '存储邮件模板的数据表';

insert into mail_send_config (
    template_type, template_subject, template_content, is_deleted, create_time, update_time, delete_time
) VALUE (
         'VERIFY_CODE', 'HermesWeb验证码', '您好,验证码为 <verify_code> ,请注意查收. 于 <verify_code_expired_at> 前有效.  ———来自HermesWeb',
         false, now(), now(), null
    );

create table mail_send_filter (
                                  id bigint not null auto_increment comment '自增主键, filter的唯一标识',
                                  template_type varchar(32) not null comment '邮件模板类型, 邮件模板的唯一标识',
                                  action int not null default 0 comment 'filter行为, 枚举值: 0 - reject; 1 - accept',
                                  uid char(36) default null comment '用户唯一标识, filter命中条件之uid',
                                  user_role int default null comment '用户角色, filter命中条件之user_role',
                                  email_account varchar(255) default null unique comment '用户邮箱, filter命中条件之email_account',
                                  is_deleted boolean default false comment '软删除标识',
                                  create_time datetime not null default current_timestamp comment '创建时间',
                                  update_time datetime not null default current_timestamp comment '修改时间',
                                  delete_time datetime default null comment '删除时间',
                                  primary key (id),
                                  unique key uni_template_type_action_uid_user_role_email_account (template_type, action, uid, user_role, email_account)
) engine = InnoDB default charset = utf8mb4 comment = '存储邮件发送过滤器信息的数据表';