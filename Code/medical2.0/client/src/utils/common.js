
/**
* 两层循环实现建树 把list数据通过 id，和parentId够造成一个树
*/
export function bulidTree(list) {
  let trees = []
  list.forEach(function(value,i){
    if("0" === value.parentId) {
      trees.push(value)
	}
	value.children = []

	list.forEach(function(child,i){
	  if(value.id === child.parentId) {
	    value.children.push(child)
	  }
	})
  })
  return trees
}

export function isLightOrDark(rgbColor) {
  let currentRgb = rgbColor.replace("rgb(", "").replace(")", "");
  let currentRgbArr = currentRgb.split(',');
  let grayLevel =  currentRgbArr[0] * 0.299 + currentRgbArr[1] * 0.587 + currentRgbArr[2] * 0.114;
  return grayLevel >= 192
}
