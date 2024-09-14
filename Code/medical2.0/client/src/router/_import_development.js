module.exports = file => {
    return resolve => require(['@/views/' + file + '.vue'], resolve).default  // vue-loader at least v13.0.0+
}
