const path = require('path')
const TerserPlugin = require('terser-webpack-plugin')

module.exports = {
  publicPath: './',
  outputDir: 'dist',
  assetsDir: 'static',
  lintOnSave: false,
  productionSourceMap: false,

  css: {
    extract: {
      filename: 'static/css/[name].[contenthash].css',
      ignoreOrder: true
    }
  },

  devServer: {
    port: 7020,
    open: true,
    client: {
      overlay: {
        warnings: false,
        errors: true,
        runtimeErrors: (error) => {
          const ignoreErrors = [
            'ResizeObserver loop completed with undelivered notifications',
            'ResizeObserver loop limit exceeded',
          ];
          if (ignoreErrors.some(e => error.message?.includes(e))) {
            return false;
          }
          return true;
        }
      }
    },
    proxy: {
      '/api': {
        target: 'http://localhost:7016',
        changeOrigin: true,
        pathRewrite: { '^/api': '' }
      }
    }
  },

  configureWebpack: {
    externals: {
      'vue-i18n': 'VueI18n',
      vue: 'Vue',
      vuex: 'Vuex',
      'element-ui': 'ELEMENT',
      'vue-router': 'VueRouter',
      'vue-dataviewer': 'vue-dataviewer',
      echarts: 'echarts',
      '@antv/g2plot': 'G2Plot',
      'umy-ui': 'UMYUI',
      jspdf: 'jspdf',
      './cptable': 'var cptable'
    },
    resolve: {
      extensions: ['.js', '.vue', '.json'],
      alias: {
        // vue$: 'vue/dist/vue.esm.js',
        '@': path.resolve(__dirname, 'src')
      },
      fallback: {
        fs: false,
        path: false,
        stream: false,
        crypto: false,
        os: false,
        url: false,
        zlib: false,
        http: false,
        https: false,
        net: false,
        tls: false,
        child_process: false,
        dgram: false,
        dns: false,
        cluster: false,
        module: false,
        vm: false
      }
    },
    optimization: {
      minimizer: [
        new TerserPlugin({
          terserOptions: {
            compress: {
              drop_console: true,
              drop_debugger: true,
              pure_funcs: ['console.log']
            }
          }
        })
      ],
      runtimeChunk: { name: 'manifest' },
      splitChunks: {
        cacheGroups: {
          defaultVendors: false,
          vendor: {
            test: /[\\/]node_modules[\\/]/,
            name: 'vendor',
            chunks: 'all',
            enforce: true
          },
          // 将大型库拆分为独立 chunk
          xlsx: {
            test: /[\\/]node_modules[\\/](exceljs|file-saver)/,
            name: 'xlsx',
            chunks: 'all',
            priority: 10
          },
          moment: {
            test: /[\\/]node_modules[\\/]moment/,
            name: 'moment',
            chunks: 'all',
            priority: 10
          }
        }
      }
    }
  },

  chainWebpack: config => {
    // 注入自定义环境变量
    try {
      const prodEnv = require('./config/prod.env')
      config.plugin('define').tap(args => {
        const defs = args[0] || {}
        Object.keys(prodEnv).forEach(key => {
          if (key !== 'NODE_ENV') {
            defs[`process.env.${key}`] = prodEnv[key]
          }
        })
        return [defs]
      })
    } catch (e) {
      console.log('No prod.env found, skipping environment variable injection')
    }

    // HTML 配置
    config.plugin('html').tap(args => {
      args[0].template = path.resolve(__dirname, 'index.html')
      args[0].favicon = 'src/assets/images/Logo.png'
      args[0].minify = {
        removeComments: true,
        collapseWhitespace: true,
        removeAttributeQuotes: true
      }
      return args
    })

    // 修复 SVG 处理：让默认 svg 规则排除 src/icons，并为 icons 目录单独使用 svg-sprite-loader
    config.module.rule('svg').exclude.add(path.resolve(__dirname, 'src/icons')).end()

    config.module
      .rule('icons')
      .test(/\.svg$/)
      .include.add(path.resolve(__dirname, 'src/icons'))
      .end()
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
      .options({
        symbolId: 'icon-[name]'
      })

    // 添加 .xlsx 文件处理规则
    config.module
      .rule('xlsx')
      .test(/\.xlsx$/)
      .use('file-loader')
      .loader('file-loader')
      .options({
        name: 'static/files/[name].[hash:8].[ext]'
      })
  }
}
