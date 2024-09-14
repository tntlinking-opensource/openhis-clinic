export function routerTree(data) {
  data.forEach(function(item) {
    delete item.children;
  });
  
  let map = new Map();
  data.forEach(function(item) {
    map.set(item.id, item)
  });
  
  let temp = [];
  data.forEach(function(item) {
    let parent = map.get(item.parent.id);
    
    if (parent) {
      (parent.children || (parent.children = [])).push(item);
    } else {
      temp.push(item);
    }
  });
  return temp;
}

export function handleFamily(tree, handle, temp = []) {
  if (!tree) return []
  for (const item of tree) {
    temp.push(item.id)
    if (handle(item)) return temp
    if (item.children) {
      const childrenArr = handleFamily(item.children, handle, temp)
      if (childrenArr.length > 0) return childrenArr
    }
    temp.pop()
  }
  return []
}
