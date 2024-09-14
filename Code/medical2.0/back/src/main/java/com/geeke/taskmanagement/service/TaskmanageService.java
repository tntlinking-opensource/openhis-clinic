package com.geeke.taskmanagement.service;


import com.geeke.common.data.Page;
import com.geeke.outpatient.entity.Blmb;
import com.geeke.sys.entity.SysFile;
import com.geeke.sys.service.SysFileService;
import com.geeke.taskmanagement.dao.TaskmangementDao;
import com.geeke.taskmanagement.dao.taskexecutorDao;
import com.geeke.taskmanagement.entity.*;
import com.geeke.utils.IdGen;
import com.geeke.utils.SessionUtils;
import com.geeke.utils.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service("taskmanageService")
@Transactional(readOnly = false)
public class TaskmanageService {

    @Autowired
    private TaskmangementDao taskmangementDao;

    @Autowired
    private SysFileService sysFileService;

    @Autowired
    private com.geeke.taskmanagement.dao.taskexecutorDao taskexecutorDao;

    @Transactional
    public int inserttask(taskmanagement task)  {
        task.setID(IdGen.uuid());
        task.setCompanyId(SessionUtils.getUser().getCompany().getId());
        task.setCreatedBy(SessionUtils.getUserJson().getString("name"));
        task.setCreatedTime(new Date());
        task.setUpdatedBy(SessionUtils.getUserJson().getString("name"));
        task.setUpdatedTime(new Date());
        task.setDelFlag("0");
//        //保存上传文件
//        List<SysFile> sysFiles = sysFileService.changeAndSaveSysFileList(fileIdUploads, task.getID());
//        if(sysFiles!=null&&sysFiles.size()>0){
//            task.setTaskaccessory(sysFiles.get(0).getId());
//        }
        return taskmangementDao.insertask(task);
    }

    public Page<taskmanagement> selecttasklist(gettaskrcvo rcvo){
        List<taskmanagement> listtotal=taskmangementDao.selecttasktotal(rcvo);
        int total=listtotal.size();
        List<taskmanagement> list=null;
        if(total>0){
            list=taskmangementDao.selecttasklist(rcvo);
        }
        if(!CollectionUtils.isEmpty(list)){
            for (taskmanagement taskmanagement : list) {
                List<taskexecutor> list1 = taskexecutorDao.list(taskmanagement.getID());
                taskmanagement.setTaskexecutors(list1);
            }
        }
        return new Page<>((long) total,list);
    }

    @Transactional
    public taskmanagement save(taskmanagement task,String[] deleteIds, MultipartFile[] fileIdUploads,String counId) throws IOException {
        if(task.getID()!=null && !"".equals(task.getID())){
            taskmanagement storage = storage(task, fileIdUploads, deleteIds, counId);
            taskmanagement release = release(task);
            return release;
        }else {
            if(Objects.equals("0",counId)){
                task.setID(IdGen.uuid());
                task.setCompanyId(SessionUtils.getUser().getCompany().getId());
                task.setCreatedBy(SessionUtils.getUserJson().getString("name"));
                task.setCreatedTime(new Date());
                task.setTaskstatus("1");//已发布
                task.setUpdatedBy(SessionUtils.getUserJson().getString("name"));
                task.setUpdatedTime(new Date());
                task.setDelFlag("0");
                int cutor=savetaskexecutor(task);//新增执行人
                int fee=savefeedbacktable(task);//新增反馈

                //保存上传文件
                List<SysFile> sysFiles = sysFileService.changeAndSaveSysFileList(fileIdUploads, task.getID());
                if(sysFiles!=null&&sysFiles.size()>0){
                    task.setTaskaccessory(sysFiles.get(0).getId());
                }
                // 根据附件ID删除附件信息
                if (null != deleteIds && deleteIds.length > 0) {
                    sysFileService.delete(deleteIds);
                }
                taskmangementDao.insertask(task);
                return task;
            }else {
                taskmanagement byId = this.taskmangementDao.getById(counId);
                //保存上传文件
                List<SysFile> sysFiles = sysFileService.changeAndSaveSysFileList(fileIdUploads, byId.getID());
                if(sysFiles!=null&&sysFiles.size()>0){
                    task.setTaskaccessory(sysFiles.get(0).getId());
                }
                // 根据附件ID删除附件信息
                if (null != deleteIds && deleteIds.length > 0) {
                    sysFileService.delete(deleteIds);
                }

                return byId;
            }
        }
    }

    public int savetaskexecutor(taskmanagement task){
        int result=0;
        for(String itme:task.getTaskregion()){
            taskexecutor cutor=new taskexecutor();
            cutor.setID(IdGen.uuid());
            cutor.setCompanyId(SessionUtils.getUser().getCompanyId());
            cutor.setCreatedBy(SessionUtils.getUserJson().getString("name"));
            cutor.setCreatedTime(new Date());
            cutor.setDelFlag("0");
            cutor.setTaskmanagementid(task.getID());
            cutor.setClinic_id(itme);
            result=taskmangementDao.savetaskexecutor(cutor);
        }
        return result;
    }

    /*
    获取反馈情况
     */
    public List<feedbacktable> getfeedbacktable(gettaskrcvo rcvo){
        return taskmangementDao.getfeedbacktable(rcvo);
    }

    /*
    新增反馈
     */
    public int savefeedbacktable(taskmanagement task){
        int result=0;
        for(String itme:task.getTaskregion()) {
            feedbacktable fee = new feedbacktable();
            fee.setId(IdGen.uuid());
            fee.setTaskmanagementid(task.getID());
            fee.setCompanyId(itme);

            fee.setCreatedBy(task.getCreatedBy());
            fee.setCreatedTime(new Date());
            fee.setUpdatedBy(task.getCreatedBy());
            fee.setUpdatedTime(new Date());
            fee.setDelFlag("0");
            fee.setExecuteschedule("0");
            fee.setAuditType("0");
            result=taskmangementDao.savefeedbacktable(fee);
        }
        return result;
    }

    /*
    任务审核
     */
    public int updateaudit(taskmanagement task){
        task.setUpdatedBy(SessionUtils.getUser().getUpdateBy());
        task.setUpdatedTime(new Date());
        task.setTaskstatus("1");
        return taskmangementDao.updateaudit(task);
    }



    /**
     * 获取文档目录的树形结构
     */

    public List<Sysusertreeentity> getusertree(gettaskrcvo gettaskrcvo) {
//        QueryWrapper<FileCatalog> wrapper = new QueryWrapper<>();
//        wrapper.eq("panels_id", panelsId);
        gettaskrcvo rcvo=new gettaskrcvo();
        rcvo.setCompanyId(gettaskrcvo.getCompanyId());
        List<Sysusertreeentity> fileCatalogs = taskmangementDao.getusertree(rcvo);
        List<Sysusertreeentity> lists = new ArrayList<>();
//        for (Sysusertreeentity fileCatalog : fileCatalogs) {
//            Sysusertreeentity vo = new Sysusertreeentity();
//            BeanUtils.copyProperties(vo,fileCatalog);
//            lists.add(vo);
//        }
        List<Sysusertreeentity> tree = createTree(fileCatalogs, fileCatalogs.get(0).getPatid());
        return tree;
    }

    /**
     * 递归建立树形结构 （算法）
     * @param lists
     * @param pid
     */
    private List<Sysusertreeentity> createTree(List<Sysusertreeentity> lists, String pid) {
        List<Sysusertreeentity> tree = new ArrayList<>();
        for (Sysusertreeentity catelog : lists) {
            if (catelog.getPatid() == pid) {
                catelog.setChirldren(createTree(lists,catelog.getId()));
                tree.add(catelog);
            }
        }
        return tree;
    }

    @Transactional
    public taskmanagement storage(taskmanagement task, MultipartFile[] fileIdUploads,String[] deleteIds,String countId) throws IOException {



        if(task.getID()!=null&&!"".equals(task.getID())){
            if("0".equals(countId)){
                //保存上传文件
                if(null!=fileIdUploads && fileIdUploads.length>0){
                    List<SysFile> sysFiles = sysFileService.changeAndSaveSysFileList(fileIdUploads, task.getID());
                    if(sysFiles!=null&&sysFiles.size()>0){
                        task.setTaskaccessory(sysFiles.get(0).getId());
                    }
                }
                // 根据附件ID删除附件信息
                if (null != deleteIds && deleteIds.length > 0) {
                    sysFileService.delete(deleteIds);
                }

                task.setUpdatedBy(SessionUtils.getUserJson().getString("name"));
                task.setUpdatedTime(new Date());
                int cutor=updatetaskexecutor(task);//新增执行人
                taskmangementDao.update(task);
                return task;
            }else {
                taskmanagement byId = this.taskmangementDao.getById(countId);
                //保存上传文件
                if(null!=fileIdUploads && fileIdUploads.length>0){
                    List<SysFile> sysFiles = sysFileService.changeAndSaveSysFileList(fileIdUploads, byId.getID());
                    if(sysFiles!=null&&sysFiles.size()>0){
                        task.setTaskaccessory(sysFiles.get(0).getId());
                    }
                }
                // 根据附件ID删除附件信息
                if (null != deleteIds && deleteIds.length > 0) {
                    sysFileService.delete(deleteIds);
                }
                return byId;
            }

       }else {
           if(Objects.equals("0",countId)){
               task.setID(IdGen.uuid());
               //保存上传文件
               if(null!=fileIdUploads && fileIdUploads.length>0){
                   List<SysFile> sysFiles = sysFileService.changeAndSaveSysFileList(fileIdUploads, task.getID());
                   if(sysFiles!=null&&sysFiles.size()>0){
                       task.setTaskaccessory(sysFiles.get(0).getId());
                   }
               }
               // 根据附件ID删除附件信息
               if (null != deleteIds && deleteIds.length > 0) {
                   sysFileService.delete(deleteIds);
               }
               task.setCompanyId(SessionUtils.getUser().getCompany().getId());
               task.setCreatedBy(SessionUtils.getUserJson().getString("name"));
               task.setCreatedTime(new Date());
             //  task.setTaskstatus("0");//待执行
               task.setUpdatedBy(SessionUtils.getUserJson().getString("name"));
               task.setUpdatedTime(new Date());
               task.setDelFlag("0");
               task.setTaskstatus("0");  //暂时保存
               int cutor=savetaskexecutor(task);//新增执行人
               taskmangementDao.insertask(task);
               return task;
           }else {
               taskmanagement byId = this.taskmangementDao.getById(countId);
               //保存上传文件
               if(null!=fileIdUploads && fileIdUploads.length>0){
                   List<SysFile> sysFiles = sysFileService.changeAndSaveSysFileList(fileIdUploads, byId.getID());
                   if(sysFiles!=null&&sysFiles.size()>0){
                       task.setTaskaccessory(sysFiles.get(0).getId());
                   }
               }
               // 根据附件ID删除附件信息
               if (null != deleteIds && deleteIds.length > 0) {
                   sysFileService.delete(deleteIds);
               }
               return byId;
           }
       }



    }

    private int updatetaskexecutor(taskmanagement task) {
        //先删除绑定的诊所
        taskexecutorDao.delete(task);

        //删除完后重新添加
        return savetaskexecutor(task);//新增执行人
    }

    /**
     * 任务修改保存后直接发布
     * @param taskmanagement
     * @return
     */
    @Transactional
    public taskmanagement release(taskmanagement taskmanagement) {
        int i =this.taskmangementDao.release(taskmanagement);
        if(i>0){
            //任务发布
            List<taskexecutor> list = taskexecutorDao.list(taskmanagement.getID());

            List<String> list1 = new ArrayList<>();

            for (taskexecutor taskexecutor : list) {
                list1.add(taskexecutor.getCompany().getId());
            }
           // list.stream().forEach(item->list1.add(item.getClinic_id()));

            taskmanagement.setTaskregion(list1);

            savefeedbacktable(taskmanagement);


            return taskmanagement;
        }
        return null;
    }

    @Transactional
    public taskmanagement delete(taskmanagement taskmanagement) {
        int i = taskmangementDao.delete(taskmanagement);

        if(i>0){
            return taskmanagement;
        }
        return null;
    }
//
//    private List<Sysusertreeentity>parentandchildren(list<Sysusertreeentity> list){
//
//        //最顶层根节点
//        List<Sysusertreeentity> rootlist = new ArrayList<>();
//        //非最顶层根节点
//        List<Sysusertreeentity> bodylist = new ArrayList<>();
//        for (Sysusertreeentity publishservicetype : list) {
//            if (StringUtils.isBlank(publishservicetype.getPatid())){
//                rootlist.add(publishservicetype);
//            }else{
//                bodylist.add(publishservicetype);
//            }
//        }
//        return gettree(rootlist,bodylist);
//    }
//
//    public List<Sysusertreeentity> gettree(List<Sysusertreeentity>rootlist, List<Sysusertreeentity>bodylist){
//        if (!judgeutil.isempty(bodylist)){
//            //声明一个map，用来过滤已操作过的数据
//            map<string,string> map = new hashmap<>(bodylist.size());
//            rootlist.foreach(parent->getchild(parent,bodylist,map));
//            return rootlist;
//        }else{
//            return rootlist;
//        }
//    }
//
//    private void getchild(publishservicetype parent,list<publishservicetype>bodylist, map<string,string> map){
//        list<publishservicetype>childlist = new arraylist<>();
//        bodylist.stream().filter(c->!map.containskey(c.getid()))
//                .filter(c->c.getparentid().equals(parent.getid()))
//                .foreach(c->{
//                    map.put(c.getid(),c.getparentid());
//                    getchild(c,bodylist,map);
//                    childlist.add(c);
//                });
//
//        parent.setchildren(childlist);
//    }





}
