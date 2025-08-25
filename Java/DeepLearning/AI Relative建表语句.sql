-- 建表
drop table if exists EDN_AI_DEVICE;

/*==============================================================*/
/* Table: EDN_AI_DEVICE                                         */
/*==============================================================*/
create table EDN_AI_DEVICE
(
   DEVICE_ID            bigint(18) not null  ,
   NAME                 varchar(60) not null  ,
   FULL_NAME            varchar(120) not null  ,
   RES_SPEC_ID          varchar(16) not null  ,
   FACILITY_ID          bigint(18),
   TOTAL_CAPACITY       int,
   FULL_CONN_CAPACITY   int(4),
   AVAIL_CAPACITY       int(4),
   primary key (DEVICE_ID)
);

alter table EDN_AI_DEVICE comment 'AI用设备表';

drop table if exists EDN_AI_FACILITY;

/*==============================================================*/
/* Table: EDN_AI_FACILITY                                       */
/*==============================================================*/
create table EDN_AI_FACILITY
(
   FACILITY_ID          bigint(18) not null  ,
   NAME                 varchar(60) not null  ,
   FULL_NAME            varchar(120) not null  ,
   RES_SPEC_ID          varchar(16) not null  ,
   MH_TYPE              varchar(16) not null,
   primary key (FACILITY_ID)
);

alter table EDN_AI_FACILITY comment 'AI用设施表';

drop table if exists EDN_AI_CABLE;

/*==============================================================*/
/* Table: EDN_AI_CABLE                                          */
/*==============================================================*/
create table EDN_AI_CABLE
(
   CABLE_ID             bigint(18) not null  ,
   NAME                 varchar(60) not null  ,
   FULL_NAME            varchar(120) not null  ,
   A_DEVICE_ID          bigint(18) not null  ,
   Z_DEVICE_ID          bigint(18) not null  ,
   DIAMETER             numeric(12,2)  ,
   LENGTH               numeric(12,2)  ,
   CAPACITY             int(4)  ,
   FULL_CONN_CORES      int(4)  ,
   A_AVAIL_CORES        int(4)  ,
   A_AVAIL_DETAIL       varchar(2000),
   Z_AVAIL_CORES        int(4)  ,
   Z_AVAIL_DETAIL       varchar(2000),
   primary key (CABLE_ID)
);

alter table EDN_AI_CABLE comment 'AI用缆线表';

drop table if exists EDN_AI_DUCT;

/*==============================================================*/
/* Table: EDN_AI_DUCT                                           */
/*==============================================================*/
create table EDN_AI_DUCT
(
   DUCT_ID              bigint(18) not null  ,
   NAME                 varchar(60) not null  ,
   FULL_NAME            varchar(120) not null  ,
   A_FACILITY_ID        bigint(18) not null  ,
   Z_FACILITY_ID        bigint(18) not null  ,
   DIAMETER             numeric(12,2)  ,
   LENGTH               numeric(12,2)  ,
   USAGE_RATE           numeric(8,2)  ,
   primary key (DUCT_ID)
);

alter table EDN_AI_DUCT comment 'AI用管道表';



drop table if exists EDN_AI_CABLE_DUCT_RELA;

/*==============================================================*/
/* Table: EDN_AI_CABLE_DUCT_RELA                                */
/*==============================================================*/
create table EDN_AI_CABLE_DUCT_RELA
(
   ID                   bigint(18) not null auto_increment ,
   CABLE_ID             bigint(18)  ,
   DUCT_ID              bigint(18)  ,
   SEQ                  int(4)  ,
   USAGE_RATE           numeric(8,2)  ,
   primary key (ID)
);

alter table EDN_AI_CABLE_DUCT_RELA comment 'AI用缆线与管道关系表';


drop table if exists EDN_AI_LINK_SPLICE;

/*==============================================================*/
/* Table: EDN_AI_LINK_SPLICE                                           */
/*==============================================================*/
create table EDN_AI_LINK_SPLICE
(
   SPLICE_ID            bigint(18)  not null  auto_increment,
   RES_SPEC_ID          varchar(32) ,
   DEVICE_ID            bigint(18) ,   
   A_CABLE_ID           bigint(18) ,
   A_CORE_NO            varchar(32) ,
   Z_CABLE_ID           bigint(18)  ,
   Z_CORE_NO            bigint(18) ,
   primary key (SPLICE_ID)
);

drop table if exists EDN_AI_LINK_TERMINATION;

/*==============================================================*/
/* Table: EDN_AI_LINK_TERMINATION                                           */
/*==============================================================*/
create table EDN_AI_LINK_TERMINATION
(
   TERMINATION_ID       bigint(18)  not null  auto_increment,
   RES_SPEC_ID          varchar(32) ,
   DEVICE_ID            bigint(18) ,
   PORT_NO              varchar(32),
   CABLE_ID             bigint(18) ,
   CORE_NO              varchar(32) ,
   primary key (TERMINATION_ID)
);




drop table if exists EDNS_AI_COST_CONF;

/*==============================================================*/
/* Table: EDNS_AI_COST_CONF                                         */
/*==============================================================*/
create table EDNS_AI_COST_CONF
(
   ID                   bigint(18) auto_increment not null,
   CATEGORY             varchar(60) not null,
   CATEGORY_DESC        varchar(256),
   TYPE                 varchar(60),
   TYPE_DESC            varchar(256),
   UNIT                 varchar(12),
   UNIT_DESC            varchar(256),
   COST                 numeric(12,2),
   primary key (ID)
);


drop table if exists EDN_AI_PORT;

/*==============================================================*/
/* Table: EDN_AI_PORT                                         */
/*==============================================================*/
create table EDN_AI_PORT
(
   PORT_ID              bigint(18) not null,
   PORT_NO              int,
   DEVICE_ID            bigint(18), 
   primary key (PORT_ID)
);


drop table if exists EDN_AI_FIBER_CORE;

/*==============================================================*/
/* Table: EDN_AI_FIBER_CORE                                         */
/*==============================================================*/
create table EDN_AI_FIBER_CORE
(
   CORE_ID              bigint(18) not null,
   CORE_NO              int,
   CABLE_ID            bigint(18), 
   primary key (CORE_ID)
);


-- 建立索引
CREATE INDEX idx_device_facility ON edn_ai_device(FACILITY_ID);
CREATE INDEX idx_device_type ON edn_ai_device(RES_SPEC_ID);
CREATE INDEX idx_device_capacity ON edn_ai_device(AVAIL_CAPACITY);

-- 光缆表索引
CREATE INDEX idx_cable_devices ON edn_ai_cable(A_DEVICE_ID, Z_DEVICE_ID);
CREATE INDEX idx_cable_avail ON edn_ai_cable(Z_AVAIL_CORES);

-- 设施表索引
CREATE INDEX idx_facility_type ON edn_ai_facility(MH_TYPE);

-- 管道表索引
CREATE INDEX idx_duct_facilities ON edn_ai_duct(A_FACILITY_ID, Z_FACILITY_ID);

-- 连接关系表索引
CREATE INDEX idx_link_resources ON edn_ai_link(A_RES_ID, Z_RES_ID);
CREATE INDEX idx_link_spec ON edn_ai_link(RES_SPEC_ID);

-- 光缆管道关系表索引
CREATE INDEX idx_cable_duct ON edn_ai_cable_duct_rela(CABLE_ID, DUCT_ID);

select distinct(device_type) from im_device where res_spec_id = 'ODB';

-- 数据
truncate table edn_ai_device;
truncate table edn_ai_facility;
truncate table edn_ai_cable;
truncate table edn_ai_duct;
truncate table edn_ai_cable_duct_rela;
truncate table edn_ai_link;

-- 插入所有的设备(ODF/ODB)
insert into edn_ai_device(device_id, res_spec_id, name, full_name, facility_id)
select device_id, res_spec_id, name, full_name, facility_id
from im_device a
where a.life_state_id = 'A' and a.project_id is null and a.res_spec_id in ('ODF', 'F_CLOSURE');

insert into edn_ai_device(device_id, res_spec_id, name, full_name, facility_id)
select device_id, res_spec_id, name, full_name, facility_id
from im_device a
where a.life_state_id = 'A' and a.project_id is null and a.res_spec_id in ('ODB') and a.device_type = ('reserve');

-- 插入所有的设施
insert into edn_ai_facility(facility_id, res_spec_id, name, full_name)
select a.facility_id, a.res_spec_id, a.name, a.full_name
from im_facility a where a.life_state_id = 'A' and a.project_id is null and a.res_spec_id = 'MANHOLE';

-- 插入缆线
insert into edn_ai_cable(cable_id, name, full_name, a_device_id, z_device_id, length)
select cable_id, name, full_name, a_device_id, z_device_id, length
from im_cable a
where a.life_state_id = 'A' and a.project_id is null and a.res_spec_id = 'F_CABLE' and a.a_device_id is not null and a.z_device_id is not null;


-- 更新缆线的直径和容量
update edn_ai_cable a set diameter = (select i.diameter from ims_fiber_cable_template i , im_cable b where b.template_id = i.TEMPLATE_ID and a.cable_id = b.CABLE_ID);
update edn_ai_cable a set capacity = (select count(1) from im_cable_unit i where i.cable_id = a.cable_id and i.res_spec_id = 'FIBER_CORE');


-- 查找缆线在a端的连接，然后用容量进行相减，得到a端的空余容量
update edn_ai_cable a 
join
(
select count(1) as cnt, g.cable_id from (
select i.TERMINATION_ID as id, i.cable_id from im_cable d, im_device b, im_port_cblunit i where d.A_DEVICE_ID = b.DEVICE_ID and i.DEVICE_ID = b.DEVICE_ID and i.cable_id = d.cable_id and i.life_state_id = 'A'
union
select f.SPLICING_ID as id, f.z_cable_id as cable_id from im_cable e, im_cblunit_cblunit f where e.cable_id = f.z_cable_id  and f.life_state_id = 'A'
) as g group by cable_id) as h on a.cable_id = h.cable_id 
set a.a_avail_cores = (a.capacity - h.cnt);

-- 查找缆线在z端的连接，然后用容量进行相减，得到z端的空余容量
update edn_ai_cable a 
join
(
select count(1) as cnt, g.cable_id from (
select i.TERMINATION_ID as id, i.cable_id from im_cable d, im_device b, im_port_cblunit i where d.Z_DEVICE_ID = b.DEVICE_ID and i.DEVICE_ID = b.DEVICE_ID and i.cable_id = d.cable_id and i.life_state_id = 'A'
union
select f.SPLICING_ID as id, f.a_cable_id as cable_id from im_cable e, im_cblunit_cblunit f where e.cable_id = f.a_cable_id  and f.life_state_id = 'A'
) as g group by cable_id) as h on a.cable_id = h.cable_id 
set a.z_avail_cores = (a.capacity - h.cnt);

-- 更新没有统计到的数据为光缆容量
update edn_ai_cable set a_avail_cores = CAPACITY where a_avail_cores is null;
update edn_ai_cable set z_avail_cores = CAPACITY where z_avail_cores is null;

-- 更新所有的Z_AVAIL_CONN_A_CORES为0
update edn_ai_cable set Z_AVAIL_CONN_A_CORES = 0;

-- 查找一根光缆在A端有连接，但是这些连接的纤芯不在Z端的连接中的数据
update edn_ai_cable a 
join
(
select count(1) as cnt, g.cable_id from (
select i.TERMINATION_ID as id, i.cable_id from im_cable d, im_device b, im_port_cblunit i where d.A_DEVICE_ID = b.DEVICE_ID and i.DEVICE_ID = b.DEVICE_ID 
and i.cable_id = d.cable_id and i.life_state_id = 'A' and i.project_id is null
and not exists(select * from im_port_cblunit j where j.cable_id = i.cable_id and j.unit_id = i.unit_id and j.device_id != b.device_id and j.life_state_id = 'A' and j.project_id is null)
and not exists(select * from im_cblunit_cblunit k where k.a_cable_id = i.cable_id and k.a_unit_id=i.unit_id and k.life_state_id = 'A' and k.project_id is null)
union
select f.SPLICING_ID as id, f.z_cable_id as cable_id from im_cable e, im_cblunit_cblunit f where e.cable_id = f.z_cable_id  and f.life_state_id = 'A' and f.project_id is null
and not exists(select * from im_port_cblunit m where m.cable_id = e.CABLE_ID  and  m.unit_id = f.z_unit_id and m.device_id != f.device_id and m.life_state_id = 'A' and m.project_id is null)
and not exists(select * from im_cblunit_cblunit n where n.A_CABLE_ID = e.cable_id and  n.a_unit_id=f.z_unit_id and n.LIFE_STATE_ID = 'A' and n.project_id is null)
) as g group by cable_id) as h on a.cable_id = h.cable_id 
set a.Z_AVAIL_CONN_A_CORES = h.cnt;

-- 更新直接连接到ODF的纤芯数据
update edn_ai_cable e 
join 
(
select count(1) as cnt, cable_id from edn_cable_link_end_info_swiss where is_link_odf = 'Y' group by cable_id
) as a
on e.cable_id = a.cable_id 
set e.FULL_CONN_CORES = a.cnt;

-- 首先更新设备表的容量为0
update edn_ai_device set FULL_CONN_CAPACITY= 0 where FULL_CONN_CAPACITY is null;
update edn_ai_device set AVAIL_CAPACITY= 0 where AVAIL_CAPACITY is null;

-- 从上行光缆来汇总所有可用容量
update edn_ai_device a
join 
(
select sum(b.z_avail_cores) as cnt, b.z_device_id from edn_ai_cable b group by b.z_device_id) as c
on a.device_id = c.Z_DEVICE_ID
set a.avail_capacity = c.cnt
where a.RES_SPEC_ID != 'ODF';


update edn_ai_device a
join 
(
select sum(b.full_conn_cores) as cnt, b.z_device_id from edn_ai_cable b group by b.z_device_id) as c
on a.device_id = c.Z_DEVICE_ID
set a.full_conn_capacity = c.cnt
where a.RES_SPEC_ID != 'ODF';

-- ODF特殊处理
update edn_ai_device a
join 
(
select count(1) as cnt, a.device_id from im_port a where not exists(select * from im_port_cblunit b where b.port_id = a.port_id) group by a.device_id
) as c
on a.device_id = c.device_id
set a.avail_capacity = c.cnt
where a.res_spec_id = 'ODF';


-- 插入管道数据
insert into edn_ai_duct(duct_id, name, full_name, length, a_facility_id, z_facility_id)
select a.pipe_channel_id as duct_id, a.name, a.full_name, a.length,b.a_facility_id, b.z_facility_id
from im_pipe_hole a, edn_duct_rela b
where a.life_state_id = 'A' and a.project_id is null and a.res_spec_id = 'PIPELINE_B' and a.pipe_channel_id = b.duct_id and b.schema_id = '1';

-- 更新管道直径和使用率
update edn_ai_duct a set diameter = (select i.diameter from ims_pipeline_block_tmpl i , im_pipe_hole  b where b.template_id = i.TEMPLATE_ID and a.duct_id = b.PIPE_CHANNEL_ID);
update edn_ai_duct a set usage_rate = (select i.space_utilization from im_routes_con i where a.full_name = i.im_routes_con_doc_id limit 1);

-- 插入光缆和管道关系表
insert into edn_ai_cable_duct_rela(cable_id, duct_id, seq)
select a.cable_id, a.pipe_channel_id as duct_id, a.seq
from im_fac_sec_cable a
where exists (select 1 from edn_ai_duct c, im_pipe_hole d  where a.pipe_channel_id = d.pipe_channel_id and d.parent_id = c.duct_id ) 
and exists(select 1 from edn_ai_cable b  where a.cable_id = b.cable_id);


-- 插入连接表,首先插入成端
insert into edn_ai_link_termination(RES_SPEC_ID, RES_ID, A_RES_SPEC_ID, A_RES_ID, A_NO, Z_RES_SPEC_ID, Z_RES_ID, Z_NO)
select b.RES_SPEC_ID , a.device_id as RES_ID, b.RES_SPEC_ID as A_RES_SPEC_ID, a.DEVICE_ID as A_RES_ID, c.seq as A_UNIT_NO, 
'F_CABLE' as Z_RES_SPEC_ID, a.cable_id as Z_RES_ID, e.NAME as Z_UNIT_NO
from im_port_cblunit a, im_device b, im_port c, im_cable d, im_cable_unit e
where a.DEVICE_ID = b.DEVICE_ID and a.CABLE_ID = d.CABLE_ID 
and a.PORT_ID = c.PORT_ID and a.UNIT_ID = e.UNIT_ID 
and a.LIFE_STATE_ID = 'A' and a.project_id is null 
and b.LIFE_STATE_ID = 'A' and b.PROJECT_ID is null 
and d.LIFE_STATE_ID = 'A' and d.PROJECT_ID is null;

-- 再插入熔接
insert into edn_ai_link(RES_SPEC_ID, RES_ID, A_RES_SPEC_ID, A_RES_ID, A_NO,Z_RES_SPEC_ID, Z_RES_ID, Z_NO)
select b.RES_SPEC_ID , a.device_id as RES_ID, 'F_CABLE' as A_RES_SPEC_ID, d.cable_ID as A_RES_ID, e.NAME as A_UNIT_NO, 
'F_CABLE' as Z_RES_SPEC_ID, f.cable_id as Z_RES_ID, g.NAME as Z_UNIT_NO
from im_cblunit_cblunit a, im_device b, im_cable d, im_cable_unit e, im_cable f, im_cable_unit g
where a.DEVICE_ID = b.DEVICE_ID and a.LIFE_STATE_ID = 'A' and a.project_id is null
and a.a_CABLE_ID = d.CABLE_ID
and a.Z_CABLE_ID = f.CABLE_ID
and a.A_UNIT_ID = e.UNIT_ID 
and a.Z_UNIT_ID = g.UNIT_ID;

-- 插入edn_ai_port

insert into edn_ai_port
select a.port_id, a.seq, a.device_id from im_port a, im_device b where a.device_id = b.device_id 
and b.life_state_id = 'A' and b.project_id is null and b.res_spec_id = 'ODF';

insert into edn_ai_port
select a.port_id, a.seq, a.device_id from im_port a, im_device b where a.device_id = b.device_id 
and b.life_state_id = 'A' and b.project_id is null and b.res_spec_id = 'ODB' and b.device_type = 'reserve';

-- 插入edn_ai_fiber_core
insert into edn_ai_fiber_core
select a.unit_id, a.name, a.cable_id from im_cable_unit a, im_cable b where a.res_spec_id = 'FIBER_CORE' and a.cable_id = b.cable_id 
and b.res_spec_id ='F_CABLE' and b.life_state_id = 'A' and b.project_id is null;


-- 查询
select * from edn_ai_device e where res_spec_id = 'ODF' and device_id = '100439';
select * from edn_ai_cable eac where eac.Z_AVAIL_CONN_A_CORES != 0;
select * from edn_ai_facility;
select * from edn_ai_cable_duct_rela eacdr ;
select * from edn_ai_duct ead where a_facility_id = '1' or z_facility_id = '1';
select * from edn_ai_link eal where eal.A_RES_SPEC_ID = 'F_CABLE';
select * from edns_ai_cost_conf eacc ;

select * from edn_ai_device e where NAME='EQU00eopm7';
select * from edn_ai_cable where a_device_id = 33317;

select * from im_pipe_hole where pipe_channel_id = '21317';
select * from edn_duct_rela where duct_id = '38873';
