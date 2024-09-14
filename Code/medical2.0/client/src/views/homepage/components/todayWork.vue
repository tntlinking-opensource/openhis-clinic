<template>
    <el-card class="index-module-card">
      <add-today-work ref='addTodayWorkForm' ></add-today-work>
      <!-- 标题 -->
      <div slot="header" class="card-title-box">
        <span class="el-card-title">我的日程</span>
        <i v-show="!isPanelSetIcon" class="el-icon-close" style="float: right; padding: 3px 0" @click="deletePanelItem"></i>
        <span class="choice-month-btn">
          <span>{{headOptions.date }}{{'(' + headOptions.ncWeek + ')'}}</span>
        </span>
      </div>

      <div class="common">
        <el-radio-group v-model="dateType" @change="dateTypeChange">
          <el-radio-button label="1">月</el-radio-button>
          <el-radio-button label="2">周</el-radio-button>
        </el-radio-group>

        <!-- 月 -->
        <span  v-if="dateType == 1">
          <el-date-picker v-model="value" type="month" :clearable="false"></el-date-picker>
          <el-button-group>
            <el-button plain @click="handleMonth('on')" icon="el-icon-arrow-left">上月</el-button>
            <el-button plain @click="getToday">今天</el-button>
            <el-button plain @click="handleMonth('next')">下月<i class="el-icon-arrow-right el-icon--right"></i></el-button>
          </el-button-group>
        </span>
      
        <!-- 周 -->
        <span  v-else>
          <el-date-picker v-model="value" type="week" format="yyyy 第 WW 周" @change="weekChange" :clearable="false"></el-date-picker>
          <el-button-group>
            <el-button plain icon="el-icon-arrow-left" @click="handleOnWeek('on')">上周</el-button>
            <el-button plain @click="getToday">今天</el-button>
            <el-button plain @click="handleOnWeek('next')">下周<i class="el-icon-arrow-right el-icon--right"></i></el-button>
          </el-button-group>
        </span>
        <i class="el-icon-s-fold" @click="fadeShow"></i>
      </div>

      <el-container class='query-form-container'>
        <el-main>
          <!-- 日历 -->
          <el-calendar v-model="value" :range="range" :class="{'weeks': dateType == '1' ? false : true}">
            <template slot="dateCell" slot-scope="{ date, data }">
              <div @click="calendarOnClick(data)" :class="['date-content', data.isSelected ? 'is-selected' : '']">
                <div>
                  <span v-if="systemTime == data.day">今天</span>
                  <span v-else :class="[isWeek(date) ? 'restRed' : '' , 'isrest']">{{ getDay(date, data) }}</span>
                  <!-- <span v-if="isWeek(date)" class="rest">休</span> -->
                  <span>{{ data.isSelected ? '✔️' : ''}}</span>
                  <div>{{solarDate2lunar(data.day)}}</div>
                  <div v-for="memoItem in dataList" :key="memoItem.data">
                    <div v-if="memoItem.date == data.day && memoItem.list && memoItem.list.length > 0" class="round">
                      <svg-icon iconClass="round"/>
                    </div>
                  </div>
                </div>
              </div>
            </template>
          </el-calendar>
        </el-main>
        <el-aside align="center" width="300px" :class="scheduleAnimation" v-show="show3">
          <div style="background-color: #f2f6fc; height: 94%; max-height: 94%;">
            <div style="padding: 10px 0px;">{{headOptions.date }}{{'(' + headOptions.ncWeek + ')'}}</div>
            <div class="block"><el-avatar shape="square" :size="100" >{{ headOptions.cDay }}</el-avatar></div>
            <div>{{headOptions.IMonthCn }}{{headOptions.IDayCn}}</div>
            <div class="schedule">
              <div class="schedule_Header common">
                <div>我的日程</div>
                <el-button circle icon="el-icon-plus" @click="schedule"></el-button>
              </div>
              <el-empty description="暂无日程" v-if="popoverList.length <= 0"></el-empty>

              <div class="schedule_List" v-else>
                <div v-for="(item,indexs) in popoverList" :key="indexs">
                  <el-alert 
                    :title="item.name" 
                    :description="'描述文字说明文字说明文字说明文字说明文字说明文字说明'"
                    :type="item.type" 
                    :closable="item.closable" 
                    style="margin-bottom: 4px;width: 98%;">
                  </el-alert>
                </div>
              </div>

            </div>
          </div>
        </el-aside>
      </el-container>
    </el-card>
</template>

<script>
import { calendar } from '@/utils/calendar' // 阴历节气转换
import addTodayWork from './addTodayWork'

export default {
  props: ['id', 'panelSetIcon'],
  data () {
    return {
      value: new Date(),
      range: null,
      dateType: '1', // 日期类型
      headOptions: {},
      panelId: this.id,
      systemTime: '',
      show3: true,
      isPanelSetIcon: this.panelSetIcon,
      showAll: false,
      popoverList: [], // 气泡框展示内容
      scheduleAnimation:'',
      dataList: [
        { date:'2022-03-09',
          list: [
            { closable: false, type: 'warning' ,name: '陆家嘴2号楼221合同到期预警'},
            { closable: true, type: 'success' ,name: '陆家嘴2号楼221合同到期预警'},
            { closable: false, type: 'error' ,name: '陆家嘴2号楼221合同到期预警'},
            { closable: true, type: 'success' ,name: '合同到期预警-xxx'},
            { closable: false, type: 'info' ,name: '陆家嘴2号楼221合同到期预警'},
            { closable: true, type: 'success' ,name: '陆家嘴2号楼221合同到期预警'},
          ],
        },
        { date:'2022-03-14',
          list: [
            {closable: false, type: 'error', name: '待办事项'}
          ],
        }
      ],
    }
  },
  components: {
    addTodayWork
  },
  watch:{
    panelSetIcon:{
      deep: true,
      handler(){
        this.isPanelSetIcon = this.panelSetIcon;
      }
    },
    value: {
      handler(newVal,oldVal) {
        if (newVal != oldVal) {
          this.headOptions = this.solarlunarDay(newVal)
        }
      }
    }
  },
  mounted () {
    this.systemTime = this.fun() // 获取yyyy-mm-dd格式的当前日期
    this.headOptions = this.solarlunarDay(new Date())
    this.calendarOnClick(this.systemTime)
  },
  methods: {
    schedule() {
      this.$refs.addTodayWorkForm.$emit('openAddGroupDeviceDialog', this.systemTime)
    },
    fadeShow() {
      this.show3 = !this.show3
      if (this.show3) {
        this.scheduleAnimation = 'move_up'
      }
    },
    // 农历显示
    solarDate2lunar(solarDate) {
      var solar = solarDate.split('-')
      var lunar = calendar.solar2lunar(solar[0], solar[1], solar[2])
      return lunar.lunarFestival || lunar.festival || lunar.Term || lunar.IDayCn
    },
    // 获取当前日期具体信息
    solarlunarDay(solarDate) {
      var timestamp = new Date(solarDate);
      var res = timestamp.toISOString().slice(0,10)
      var solar = res.split('-')
      var lunar = calendar.solar2lunar(solar[0], solar[1], solar[2])
      return lunar
    },
    // 查看更多，获取当前日期所有日程
    getAll() {
      this.popoverList = [
        { closable: true, type: 'success', name: '合同到期预警-xxx', },
        { closable: false, type: 'warning', name: 'LS/12/1210待抄表'},
        { closable: false, type: 'info', name: 'GM/14/1410待抄表'},
        { closable: true, type: 'success', name: '陆家嘴2号楼221合同到期预警'},
        { closable: true, type: 'success', name: 'LS/12/1210待抄表'},
        { closable: false, type: 'error', name: 'LS/12/1210待抄表'},
      ]
    },
    // 删除面板项发送事件
    deletePanelItem () {
      this.$emit('deletePanelItemEvent', this.panelId)
    },
    // 选中具体待办事项
    getMemorandum (memoItem) {
      console.log(memoItem);
    },
    // 点击日历块
    calendarOnClick(data) {
      console.log(data);
      this.popoverList = []
      let calendardata;
      this.dataList.forEach(item => {
        calendardata = typeof (data) == 'object' ? data.day : data
        if (item.date == calendardata) {
          this.popoverList = item.list
          return
        } 
      })
    },
    // 跳转today
    getToday() {
      if (this.dateType == '2') { // 周
        var weeks = this.GetMonday(new Date())
        weeks = new Date(Date.parse(weeks))
        this.weekChange(weeks) // 跳转至当前日所在周
      } else {
        this.range = null
      }
      this.calendarOnClick(this.systemTime)
      this.value = new Date()
    },
    // 切换上下月
    handleMonth(type) {
      var month = this.getLastMonth(this.value, type)
      this.value = new Date(Date.parse(month))
    },
    getLastMonth(Time,type) {
      var year = Time.getFullYear();   // 当前年：四位数字
      var month = Time.getMonth() + 1;     // 当前月：0-11
      if (type == 'on' ) {// 如果是1，上一个月就是去年的12月
        if (month == 1) {
          year -= 1;
          month = 12;
        } else {
          month -= 1
        }
      } 
      if (type == 'next' ) {// 如果是12上一个月就是去年的12月
        if (month == 12) {
          year += 1;
          month = 1;
        } else {
          month += 1;
        }
      }
      month = month < 10 ? ('0' + month) : month;   // 月份小于10则追加个0
      let lastYearMonth = year + '-' + month;
      return lastYearMonth;
    },
    // 切换 月/周 显示
    dateTypeChange(val) {
      if (val == '1') { // 月
        this.range = null
      } else { // 周
        var weeks = this.GetMonday(this.value)
        this.value = new Date(Date.parse(weeks))
        this.weekChange(this.value)
      }
    },
    // 选择周
    weekChange(val) {
      let timeStamp = val.getTime(); // 标准时间转为时间戳，毫秒级别
      this.range = []
      this.range[0] = this.fun(timeStamp ); // 开始时间
      this.range[1] = this.fun(timeStamp + (24 * 60 * 60 * 1000) * 6) // 结束时间
    },
    // 切换上下周
    handleOnWeek(val) {
      var weeks = this.GetMonday(this.value)
      weeks = new Date(weeks).getTime()
      if (val == 'on') { // 上周
        weeks = this.fun(weeks - (24 * 60 * 60 * 1000) * 7)
      } else if (val == 'next'){ // 下周
        weeks = this.fun(weeks + (24 * 60 * 60 * 1000) * 7)
      }
      this.value = new Date(Date.parse(weeks))
      this.weekChange(this.value)
    },
    // 获取日期当前周的周一：日历控件的时间范围，开始时间必须是周一，结束时间必须是周日
    GetMonday(dd) {
      var week = dd.getDay(); // 获取时间的星期数
      var minus = week ? week - 1 : 6;
      dd.setDate(dd.getDate() - minus); // 获取minus天前的日期
      var res = dd.toISOString().slice(0,10)
      return res
    },
    // 时间戳转为 yyyy-mm-dd
    fun(unixtimestamp) {
      var timestamp = unixtimestamp ? new Date(unixtimestamp): new Date();
      var res = timestamp.toISOString().slice(0,10)
      return res
    },
    // 处理日期
    getDay(date) {
      return date.getDate()
    },
    // 判断是否周六日
    isWeek(date) {
      return date.getDay() === 6 || date.getDay() === 0
    },
  }
}
</script>

<style lang="scss" scoped>
/deep/ .el-calendar__header { // 移除组件头部
  display: none;
}
.index-module-card.el-card /deep/ .el-card__header {
  padding: 10px 16px!important;
  .card-close, .card-setting {
    float: right;
    padding: 3px 0
  }
}
.el-card {
  height: 100%;
}
.choice-month-btn {
  display: inline;
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
}
.card-title-box {
  position: relative;
}
.el-card-title {
  font-weight: bold;
}
.weeks /deep/ .el-calendar-table .el-calendar-day {
  height: 515px;
  padding: 0px !important;
}
/deep/ .el-calendar-table .el-calendar-day {
  padding: 0px !important;
}
/deep/ .el-calendar__header {
  padding: 0px 20px 8px 20px;
}
/deep/ .el-calendar-table thead th {
  padding: 5px 0px;
}
/deep/ .el-card__body {
  padding: 8px;
}
.date-content {
  height: 100%;
  text-align: center;
  font-size: 14px;
}
.restRed {
  color: rgb(250, 124, 77);
}
.isrest {
  font-size: 18px;
}
// .date-content .rest {
//   color: #fff;
//   border-radius: 50%;
//   background: rgb(250, 124, 77);
//   width: 20px;
//   height: 20px;
//   line-height: 20px;
//   display: inline-block;
//   font-size: 12px;
//   margin-left: 5px;
// }
.is-selected {
  color: #07519b;
}
.query-form-container {
  padding: 0px;
  margin: 0px;
  overflow: hidden;
  margin-top: 5px;
}
/deep/ .el-avatar {
  background: #d1d2d5;
  font-size: 36px ;
}
.round { 
  fill: #408ad3;
  font-size: 12px;
}
.el-icon-s-fold {
  font-size: 22px;
  cursor: pointer;
}
.schedule {
  .schedule_List {
    text-align:left;
    padding-left: 8px; 
    overflow: auto; 
    height: 300px;
    min-height: 320px;
  }
  .schedule_Header {
    margin: 5px 10px;
    padding: 10px 8px;
    font-size: 18px;
    background-color: #e4e7ed;
  }
}
.common {
  display: flex;
  justify-content: space-between;
  align-content: center;
}    

.move_up {
  animation: move_up .3s linear; 
  -webkit-animation: move_up .3s linear; 
}

@keyframes move_up {
  from {
    transform: translate(300px,0);
  }
  to {
    transform: translate(-100px -10px);
  }
}
</style>
