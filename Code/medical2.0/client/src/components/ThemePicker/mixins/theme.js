// const version = ELEMENT.version; // element-ui 版本号
const version = '2.15.6'; // element-ui 版本号
const ORIGINAL_THEME = '#409EFF' // 目前 element-ui 默认色值

export default function () {
  return {
    data() {
      return {
        chalk: '',
        themeVal: ORIGINAL_THEME
      }
    },
    created() {
      this.themeVal = this.theme;
      if (this.theme === ORIGINAL_THEME) {
        this.themeChange(this.theme, ORIGINAL_THEME);
      }
    },
    watch: {
      themeVal(val, oldVal) {
        this.CHANGE_SETTING({ key: 'theme', value: val });
        this.themeChange(val, oldVal);
      }
    },
    computed: {
      ...Vuex.mapGetters(["theme"])
    },
    methods: {
      ...Vuex.mapMutations({
        CHANGE_SETTING: (commit, data) => commit('settings/CHANGE_SETTING', data)
      }),
  
      async themeChange(val, old) {
        const oldVal = this.chalk ? this.themeVal : old
        if (typeof val !== 'string') return
        const themeCluster = this.getThemeCluster(val.replace('#', ''))
        const originalCluster = this.getThemeCluster(oldVal.replace('#', ''))
    
        const $message = this.$message({
          message: '主题色切换',
          customClass: 'theme-message',
          type: 'success',
          duration: 0,
          iconClass: 'el-icon-loading'
        })
    
        const getHandler = (variable, id) => {
          return () => {
            const originalCluster = this.getThemeCluster(ORIGINAL_THEME.replace('#', ''))
            const newStyle = this.updateStyle(this[variable], originalCluster, themeCluster)
        
            let styleTag = document.getElementById(id)
            if (!styleTag) {
              styleTag = document.createElement('style')
              styleTag.setAttribute('id', id)
              document.body.appendChild(styleTag)
            }
            styleTag.innerText = newStyle
          }
        }
    
        if (!this.chalk) {
          // const url = `https://unpkg.zhimg.com/element-ui@${version}/lib/theme-chalk/index.css`
          const url = 'static/lib/element/theme-chalk@2.15.6.css'
          await this.getCSSString(url, 'chalk')
        }
    
        const chalkHandler = getHandler('chalk', 'chalk-style')
    
        chalkHandler()
    
        const styles = [].slice.call(document.querySelectorAll('style'))
        .filter(style => {
          const text = style.innerText
          return new RegExp(oldVal, 'i').test(text) && !/Chalk Variables/.test(text)
        })
        styles.forEach(style => {
          const { innerText } = style
          if (typeof innerText !== 'string') return
          style.innerText = this.updateStyle(innerText, originalCluster, themeCluster)
        })
    
        this.$emit('change', val)
    
        $message.close()
      },
      updateStyle(style, oldCluster, newCluster) {
        let newStyle = style;
        oldCluster.forEach((color, index) => {
          newStyle = newStyle.replace(new RegExp(color, "ig"), newCluster[index]);
        });
        return newStyle;
      },
      getCSSString(url, variable) {
        return new Promise(resolve => {
          const xhr = new XMLHttpRequest()
          xhr.onreadystatechange = () => {
            if (xhr.readyState === 4 && xhr.status === 200) {
              this[variable] = xhr.responseText.replace(/@font-face{[^}]+}/, '')
              resolve()
            }
          }
          xhr.open('GET', url)
          xhr.send()
        })
      },
      getThemeCluster(theme) {
        const tintColor = (color, tint) => {
          let red = parseInt(color.slice(0, 2), 16);
          let green = parseInt(color.slice(2, 4), 16);
          let blue = parseInt(color.slice(4, 6), 16);

          if (tint === 0) {
            // when primary color is in its rgb space
            return [red, green, blue].join(",");
          } else {
            red += Math.round(tint * (255 - red));
            green += Math.round(tint * (255 - green));
            blue += Math.round(tint * (255 - blue));

            red = red.toString(16);
            green = green.toString(16);
            blue = blue.toString(16);

            return `#${red}${green}${blue}`;
          }
        };

        const shadeColor = (color, shade) => {
          let red = parseInt(color.slice(0, 2), 16);
          let green = parseInt(color.slice(2, 4), 16);
          let blue = parseInt(color.slice(4, 6), 16);

          red = Math.round((1 - shade) * red);
          green = Math.round((1 - shade) * green);
          blue = Math.round((1 - shade) * blue);

          red = red.toString(16);
          green = green.toString(16);
          blue = blue.toString(16);

          return `#${red}${green}${blue}`;
        };

        const clusters = [theme];
        for (let i = 0; i <= 9; i++) {
          clusters.push(tintColor(theme, Number((i / 10).toFixed(2))));
        }
        clusters.push(shadeColor(theme, 0.1));
        return clusters;
      }
    }
  }
}
