<template>
  <div>
    <el-card shadow="always">
      <div class="card-header">
        <span class="card-title">不合格处方查询</span>
        <div>
          <el-button style=" padding: 3px 0" type="text" @click="onClearAll">忽略全部</el-button>
          <el-badge :value="total<10?total:'10+'" class="badge" @click="onOpenAll"></el-badge>
        </div>
      </div>
      <div v-for="(item,index) in data.slice(0, 3)" :key="index" class="content-item">
        <a @click="onClickContent(item)" class="content">
          {{item.content}}
        </a>
        <span class="btn">
              <a @click="onClickOne(item)">{{btnOne}}</a>
              <a @click="onClickTwo(item)" style="margin-left:5px;">{{btnTwo}}</a>
        </span>
      </div>
    </el-card>
  </div>
</template>

<script>
  export default {
    name: 'home-card',
    components: {
    },
    data() {
      return {};
    },
    props: {
      total: {
        type: Number,
        default: 0
      },
      data:{
        type: Array,
        default: []
      },
      btnOne:{
        type: String,
        default: "查看"
      },
      btnTwo:{
        type: String,
        default: "忽略"
      }
    },
    watch:{
    },
    methods: {
      onClearAll(){
        this.$emit('clear-all',this.data)
      },
      onOpenAll(){
        this.$emit('open-all')
      },
      onClickContent(item){
        this.$emit('click-content',item)
      },
      onClickOne(item){
        this.$emit('click-one',item)
      },
      onClickTwo(item){
        this.$emit('click-two',item)
      },
    },
    mounted() {
    }
  }
</script>

<style lang="scss" scoped>
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }
  .card-title{
    font-weight:bold;
    color: #018cb7;
  }
  .badge{
    height: auto;
    line-height:0%;
  }
  .content-item {
    padding: 10px 0 0 0;
    font-size: 14px;
    width: 100%;
    text-align: left;
    clear:both;
  }
  .content{
    display:inline-block;

    text-align: left;
    width: calc(100% - 90px);
    overflow:hidden;
    word-break:keep-all;
    white-space:nowrap;
    text-overflow:ellipsis;
    text-decoration:none;
    color:#333;
    clear:both;
  }
  .content:hover{
    color:#018cb7;
    cursor: pointer;
  }

  .btn{
    display:inline;
    float: right;

    clear:both;
  }
  .btn a{
    text-decoration:none;
    color:#333;
  }
  .btn a:hover{
    color:#018cb7;
    cursor: pointer;
  }
</style>
