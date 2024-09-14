/**
 * Created by xieli on 2018/7/12 0012.
 */

const mixins={
  methods:{
      prompt(msg){ //提示
      this.$message({
        message: msg,
        duration:1000,

      });
    },
    notice(msg){
      this.$message({
        message: msg,
        type: 'error',
        duration:1000,
      });
    },
    success(msg){
      this.$message({
        message: msg,
        type: 'success',
        duration:1000,
      });
    },

  }
}

export {mixins}
