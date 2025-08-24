## 需求描述：
需要提供一个查询能力，根据指定的人井，找到需要指定光纤数的光路，并能通过光路连接到ODF，如果当前人井没有设备，则需要根据管道网找到一个最近距离的人井，且人井内的设备可用容量大于、等于指定光纤请求数，输出接入设备ID，和指定人井到可用设备所在人井的路由

## 模型说明：

### edn_ai_device

设备表，字段包括：

* DEVICE_ID：设备的主键
* NAME：设备名称
* FULL_NAME：设备的编码
* RES_SPEC_ID：表示设备类型，包括ODF、ODB、F_CLOSURE(Fiber Closure)
* FACILITY_ID：表示当前设备所在的人井的ID
* AVAIL_CAPACITY：表示当前设备上有多少可用纤芯，也是以当前设备为Z端的光缆的Z_AVAIL_CORES之和

### edn_ai_port
端口表，设备类型为ODB，ODF的设备有端口，F_CLOSURE就没有端口

* PORT_ID：端口的主键
* PORT_NO：端口的编码
* DEVICE_ID：端口所属的设备的主键

### edn_ai_cable

光缆表，字段包括：

* CABLE_ID：光缆的主键
* NAME：光缆名称
* FULL_NAME：光缆的编码
* A_DEVICE_ID：A端设备的ID
* Z_DEVICE_ID：Z端设备的ID
* DIAMETER：光缆的直径
* LENGTH：光缆的长度
* CAPACITY：光缆的芯数
* Z_AVAIL_CORES：Z端可用的纤芯数
* Z_AVAIL_CONN_A_CORES：标识Z端的没有连接的芯数，在A端已经连接的数量，这样，就可以知道上行是否需要做连接

### edn_ai_fiber_core

纤芯表，字段包括：

* CORE_ID：纤芯的主键
* CORE_NO：纤芯的编码
* CABLE_ID：纤芯所属的光缆的主键

### edn_ai_facility

人井表，字段包括：

* FACILITY_ID：人井的主键
* NAME：人井名称
* FULL_NAME：人井的编码
* MH_TYPE：人井类型，分为KES/KS/PS/空

### edn_ai_duct

管道表，注意，这里面A/Z没有方向性，字段包括：

* DUCT_ID：管道的主键
* NAME：管道的名称
* FULL_NAME：管道的编码
* A_FACILITY_ID：A端人井的ID
* Z_FACILITY_ID：Z端人井的ID
* DIAMETER：管道的直径
* LENGTH：管道的长度
* USAGE_RATE：管道的占用率，管道占用率是用内部的光缆的截面积之和除以管道截面积的百分比值

### edn_ai_cable_duct_rela

光缆和管道的关系表，一个光缆可以经过多个管道，字段如下：

* CABLE_ID：光缆的主键
* DUCT_ID：管道的主键
* SEQ：当前管道在沿着光缆A到Z的方向经过的所有管道中的顺序
* USAGE_RATE：当前光缆在当前管道中的占用率

### edn_ai_link

连接表，表示设备端口和光缆纤芯的连接关系，注意，这里面A/Z没有方向性：

* RES_SPEC_ID：当前连接所在的设备类型
* RES_ID：当前连接所在的设备的ID
* A_RES_SPEC_ID：连接的A端资源，可能为设备，也可能为光缆
* A_RES_ID：连接的A端资源的ID
* A_NO：A端连接的端口或者纤芯的序号
* Z_RES_SPEC_ID：连接的Z端资源，可能为设备，也可能为光缆
* Z_RES_ID：连接的Z端资源的ID
* Z_NO：Z端连接的端口或者纤芯的序号

### edns_ai_cost_conf

成本的定义表，定义各种动作的成本：

* CATEGORY：动作大类，包括
    * NEW_DUCT：表示新建管道
    * NEW_OSC：表示新建设备
    * NEW_F_CABLE：表示新建光缆
    * OPEN_MANHOLE：表示经过人井需要打开井盖
    * SPLICE_PRE：表示新建连接时的固定开销
    * SPLICE：表示新建连接时每个连接的开销
* TYPE：对动作大类的细分，包括：
    * 对于NEW_F_CABLE来说，TYPE即为芯数，不同芯数光缆的价格不同
    * 对于OPEN_MANHOLE来说，TYPE即为经过的人井的类型，不同类型人井开井盖价格不同
* UNIT：报价模式，m即按照每米报价，pcs即为按个数报价
* COST：单价值

## 需求详细：

你是一个资深电信ODN网络GNN建模专家，现需要请你基于下面某电信运营商ODN网络情况进行GNN建模，预训练模型，用于快速从靠近用户的一个人井出发，找到一条n芯可连接到ODF成本最低的光路,通过java代码输出完整的代码
某电信运营商ODN网络情况：
1. 在ODN P2P组网中，有人井、管道、光缆、Fiber Clousre、ODB、ODF。
2. 人井通过管道连接起来（人井类型分ES、KES、PS或其他），两个人井间可存在一到多根管道，管道无方向性，但管道有长度。
3. 接头盒放在人井里，光缆铺设在管道中，一根光缆可穿越多个人井间的管道，两端分别接在设备上。
4. 两个设备间可存在0到多根光缆连接。
5. 光缆有方向性，A端靠近ODF，Z端靠近用户。
6. 一根光缆内有多根纤芯。
7. 一个设备实现将不同下行光缆的多根纤芯接到同一根上行光缆的纤芯，也可将一根下行光缆的不同纤芯接到不同上行光缆的不同纤芯。
8. 连到设备上的光缆，不一定将所有的纤芯都通过设备与其它光缆的纤芯连接起来，而是留一部份备用，在后续根据实际业需要灵活调整与不同的光缆的纤芯连接。
9. ODB上的端口可以连接两次，分别是下行和上行，如果找到的可用设备是ODB，则需要当前端口的上行已经连接了才可以使用，使用则是连接到这个端口上
10. F_CLOSURE的容量表示F_CLOSURE的所有上连光缆的可用芯数之和，当找到的可用设备是F_CLOSURE时，则直接和上连的可用纤芯进行连接
11. ODF的端口可以连接一次，如果找到的可用设备是ODF，需要保证端口没有其余连接
12. 中间的设备上下行之间，如果都存在可用的纤芯，则可以补充连接，从而达到通路