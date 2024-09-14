<template>
  <div  style="text-align:left" >
    list窗口的基础组件类，处理列头拖动排序、导出数据的列头数据，以及列表显示个性化配置。
  </div>
</template>
<script>
import BaseUI from './baseUI'
import { listColumnView, saveColumnView, showDefaultColumnView, showAllColumnView } from '@/api/common/columnView'
export default {
  extends: BaseUI,
  name: 'MainUI',
  data() {
    return {
      columnViews: [],
      oprColumnWidth: 140, // 操作列列宽，默认140px，在子类中重写修改
      oprColumnDefaultWidth: null,    // 保存操作列默认列宽
      tableId: '0',       // 在子类中覆写
      dragState: {
        startIndex: -1, // 拖动起始元素的index
        endIndex: -1, // 拖动结束元素的index
        afterMoveIndex: -1, // 拖动后元素的index
        dragging: false, // 是否正在拖动
        direction: null, // 拖动方向
        moveTableOutsideBack: false, // 拖出到table外之后又拖回来
        startClassName: '',          // 保存拖动起始列的class
        startCursor: '',            // 保存拖动列的光标形状cursor
        startShowIndex: -1          // 记录拖动起始元素index在table中显示的列数
      },
      curentRow: {},
      moreCodition: false,     // 搜素框是否更多查询条件
      moreParm: {   // 更多条件值
          groupOne: [
            {logic: 'AND', column: null, queryType: '', value: null, operations: []},
            {logic: 'AND', column: null, queryType: '', value: null, operations: []},
            {logic: 'AND', column: null, queryType: '', value: null, operations: []}
          ],
          groupTwo: [
            {logic: 'AND', column: null, queryType: '', value: null, operations: []},
            {logic: 'AND', column: null, queryType: '', value: null, operations: []},
            {logic: 'AND', column: null, queryType: '', value: null, operations: []}
          ],
          groupLogic: 'OR'
      }
    }
  },
  methods: {
    async mainUIInit() {
      this.oprColumnDefaultWidth = this.oprColumnWidth  // 保存操作列默认列宽
      if (this.tableId && this.tableId != '0') {
        this.setLoad()
        listColumnView(this.$route.meta.routerId, this.tableId).then(responseData => {
          if(responseData.code == 100) {
              console.log(responseData.data,'3');
            this.initViewColumn(responseData.data)
          } else {
            this.showMessage(responseData)
          }
          this.resetLoad()
        }).catch(error => {
          this.outputError(error)
        })
      }
    },
    // 相应显示历史记录按钮
    onShowHistory(index, row) {
      this.curentRow = {id: row.id, name: row.name}
    },
    // 保存个性化显示列
    saveColumn(column) {
      column['tableId'] = this.tableId
      column['routerId'] = this.$route.meta.routerId
      this.setLoad()
      saveColumnView(column).then(responseData => {
        if(responseData.code != 100) {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    // 显示所有列
    showAllColumn() {
      this.setLoad()
      showAllColumnView(this.$route.meta.routerId, this.tableId).then(responseData => {
        if(responseData.code == 100) {
            console.log(responseData.data,'2');
          this.initViewColumn(responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    // 显示默认列
    showDefaultColumn() {
      this.setLoad()
      showDefaultColumnView(this.$route.meta.routerId, this.tableId).then(responseData => {
        if(responseData.code == 100) {
          this.oprColumnWidth = this.oprColumnDefaultWidth  // 还原操作列默认列宽
          console.log(responseData.data,'1');
          this.initViewColumn(responseData.data)
        } else {
          this.showMessage(responseData)
        }
        this.resetLoad()
      }).catch(error => {
        this.outputError(error)
      })
    },
    // drag_table在渲染表头时调用
    renderHeader (h, { column, $index }) {
      // 这里可以根据$index的值来对自身需求进行修改，
      return h('span', {
          'class': ['thead-cell'],
          on: {
            mousedown: ($event) => { this.handleMouseDown($event, column) },
            mouseup: ($event) => { this.handleMouseUp($event, column) },
            mousemove: ($event) => { this.handleMouseMove($event, column) }
          }
        },
        [
          // 添加 <a> 用于显示表头 label
          h('a', column.label),
          // 添加一个空标签用于显示拖动动画
          h('span', {
            'class': ['virtual']
          })
        ]
      )
    },


    // 按下鼠标开始拖动
    handleMouseDown (e, column) {
      // 判断是鼠标左键
      if (e.button === 0) {
        // 拖动改变列宽
        if(document.body.style.cursor == "col-resize") {
          return
        }

        this.dragState.dragging = true
        this.dragState.startIndex = parseInt(column.columnKey)
        this.dragState.startShowIndex = this.getShowIndex(this.dragState.startIndex)
        // console.log(`开始移动的位置 ${this.dragState.startIndex} 实际显示列 ${this.dragState.startShowIndex}`)
        // 给当前要拖动列的th设置class 和 cursor
        let thElement = document.querySelectorAll('.drag_table table thead tr th')[this.dragState.startShowIndex]
        this.dragState.startClassName = thElement.className
        thElement.className = 'dragging_column'
        this.dragState.startCursor = thElement.style.cursor
        thElement.style.cursor = 'move'
        // 给拖动时的虚拟容器添加宽高
        let table = document.getElementsByClassName('drag_table')[0]
        let virtual = document.getElementsByClassName('virtual')
        // 设置每一列的位置，以及宽度、高度
        for (let item of virtual) {
          // 与<th>的起始位置一样
          item.style.top = item.parentElement.parentElement.parentElement.getBoundingClientRect().y + 'px'
          item.style.left = item.parentElement.parentElement.parentElement.getBoundingClientRect().x + 'px'
          item.style.height = table.clientHeight + 'px'
          item.style.width = item.parentElement.parentElement.parentElement.clientWidth + 'px'
        }
        this.dragState.moveTableOutsideBack = false
      }
    },

    // 拖动中
    handleMouseMove (e, column) {
      // 判断是鼠标左键 并且触发过MouseDown事件
      if (e.button === 0 && this.dragState.startIndex > -1) {
        if (this.dragState.dragging) {
          let currentIndex = parseInt(column.columnKey) // 拖动的当前列index
          if (currentIndex !== this.dragState.startIndex) {
            this.dragState.direction = currentIndex - this.dragState.startIndex < 0 ? 'left' : 'right' // 判断拖动方向
            this.dragState.afterMoveIndex = currentIndex
          } else {
            this.dragState.direction = null
          }
        } else {
          return false
        }
      }
    },

    // 鼠标放开结束拖动
    handleMouseUp (e, column) {
      // 判断是鼠标左键 并且触发过MouseDown事件
      if (e.button === 0 && this.dragState.startIndex > -1) {
        // 拖出当前table外之后又拖回来，不再进行易位操作（拖出去时已处理）
        if (this.dragState.moveTableOutsideBack) {
          return false
        } else {
          this.dragState.endIndex = parseInt(column.columnKey) // 记录结束列index
          // console.log(`结束移动的位置 ${this.dragState.endIndex}`)
          if (this.dragState.startIndex !== this.dragState.endIndex) {
            this.dragColumn(this.dragState)
          }
          this.finishDragInit()
        }
      }
    },

    // 拖动到当前table之外的处理
    moveTableOutside () {
      if (this.dragState.dragging) {
        this.dragState.endIndex = this.dragState.startIndex
        // console.log(`已移动到table外，结束移动的位置 ${this.dragState.endIndex}`)
        if (this.dragState.startIndex !== this.dragState.endIndex) {
          this.dragColumn(this.dragState)
        }
        this.finishDragInit()
        this.dragState.moveTableOutsideBack = true
      }
    },

    // 拖动易位
    dragColumn ({ startIndex, endIndex, direction }) {
      // 判断是向左移动还是向右移动
      let column = this.columnViews[startIndex]
      if (direction === 'left') {
        if(endIndex == 0) {
          column.sort = this.columnViews[endIndex].sort/2
        }else{
          column.sort = (this.columnViews[endIndex - 1].sort + this.columnViews[endIndex].sort)/2
        }
        this.columnViews.splice(endIndex, 0, this.columnViews[startIndex])
        this.columnViews.splice(startIndex + 1, 1)
      } else {
        if(endIndex == this.columnViews.length - 1) {
          column.sort = (this.columnViews[endIndex].sort + (this.columnViews.length + 1)*10000)/2
        }else{
          column.sort = (this.columnViews[endIndex].sort + this.columnViews[endIndex + 1].sort)/2
        }
        this.columnViews.splice(endIndex + 1, 0, this.columnViews[startIndex])
        this.columnViews.splice(startIndex, 1)
      }
      this.saveColumn(column)
    },

    // 拖动完成后的初始化
    finishDragInit () {
      // 给当前要拖动列的th还原class 和 cursor
      let thElement = document.querySelectorAll('.drag_table table thead tr th')[this.dragState.startShowIndex]
      thElement.className = this.dragState.startClassName
      thElement.style.cursor = this.dragState.startCursor
      // 再次初始化拖动状态
      this.dragState = {
        startIndex: -1,
        endIndex: -1,
        afterMoveIndex: -1,
        dragging: false,
        direction: null,
        moveTableOutsideBack: false,
        startClassName: '',
        startCursor: '',
        startShowIndex: -1
      }
    },

    // 动态给表头单元格添加 class，实现拖动中的虚线效果
    headerCellClassName ({ column, columnIndex }) {
      return columnIndex === this.getShowIndex(this.dragState.afterMoveIndex) ? `drag_active_${this.dragState.direction}` : ''
    },

    // 动态给表头单元格th添加class，实现拖动中的背景
    cellClassName ({ column, columnIndex }) {
       return (columnIndex === this.dragState.startShowIndex ? `dragging_column` : '')
    },

    initViewColumn(columns) {
      this.columnViews.splice(0, this.columnViews.length)
      for (var i = 0; i <= columns.length - 1; i++) {
        if(columns[i].prop == 'operate_column') {   // 操作列
	        this.oprColumnWidth = columns[i].width
        } else {
          this.columnViews.push(columns[i])
        }
      }
      console.log(this.columnViews,'000');
    },

    /* 拖动列边距响应事件 */
    onChangeWidth(newWidth, oldWidth, column, event) {
      if(column.columnKey) {
        this.columnViews[column.columnKey].width = newWidth
        this.saveColumn(this.columnViews[column.columnKey])
      } else {  // 操作列
        this.oprColumnWidth = newWidth
        this.saveColumn({
          width: this.oprColumnWidth,
          prop: 'operate_column',
          display: false,
          sort: (this.columnViews.length + 1) * 10000
        })
      }
    },

    /* 获取实际显示的列数 */
    getShowIndex(index) {
      let showIndex = -1
      for (var i = 0; i <= index; i++) {
        if(this.columnViews[i].display) {
          showIndex = showIndex + 1
        }
      }
      return showIndex
    },

    /* 导出excel的列表显示列头数组 */
    getHeads(){
      var heads = []
      for (var i = 0; i <= this.columnViews.length - 1; i++) {
        if(this.columnViews[i].display) {
          heads.push(this.columnViews[i].label)
        }
      }
      return heads
    },
    /* 导出excel的列表显示列数据字段数组 */
    getFilterVal(){
      var filterVal = []
      for (var i = 0; i <= this.columnViews.length - 1; i++) {
        if(this.columnViews[i].display) {
          filterVal.push(this.columnViews[i].prop)
        }
      }
      return filterVal
    },

    onMoreCodition() {
      this.moreCodition = !this.moreCodition
    }
  },
  mounted() {
    this.mainUIInit()
  }
}
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style lang="scss">
.drag_table th {}
.virtual {
  position: fixed;
  display: block;
}
.drag_active_left .virtual {
  cursor: move;
  border-left: 1px dotted #666;
  z-index: 99;
}
.drag_active_right .virtual {
  cursor: move;
  border-right: 1px dotted #666;
  z-index: 99;
}
.thead-cell {
  display: inline-flex;
  flex-direction: column;
}
.thead-cell:before {
  content: "";
  position: absolute;
  top: 0;
  left: 0;
  bottom: 0;
  right: 0;
}
.dragging_column {
  background-color: #f3f3f3 !important;
}
</style>
