~~~bash
# 查看提交历史,commit对应的hash值
git log --oneline

# 选中需要改的commit的前一个commit的hash
# 例如需要修改2281bc3,这里的值就是下面的5a99d96
git rebase -i 5a99d96

# 进去之后i编辑，选择需要修改的commit,将pick修改为edit,Esc,:wq保存退出
# Git会自动应用前面的提交，然后暂停在你指定为edit的那个提交点，让你可以进行修改。
# 对工作区的文件进行修改，添加或删除文件,然后使用git add <修改的文件>git commit --amend将当前的修改应用到这次的commit


git rm --cached grafana/grafana-enterprise-11.1.0.linux-amd64.tar.gz

git rm --cached grafana/prometheus/prometheus-2.53.0.linux-amd64.tar.gz

git rm --cached grafana/prometheus/prometheus-2.53.0.windows-amd64.zip

# git rm -r --cached grafana/prometheus 不能执行成功
# 本地删除标红的文件
# 
git commit --amend

# 完成edit的修改
git rebase --continue


~~~

![image-20240819093026377](http://47.101.155.205/image-20240819093026377.png)

![git rebase -i 5a99d96](http://47.101.155.205/image-20240819093229114.png)

![image-20240819095404028](http://47.101.155.205/image-20240819095404028.png)

![删除之后，有未add文件或commit的](http://47.101.155.205/image-20240819095456539.png)

![image-20240819095937331](http://47.101.155.205/image-20240819095937331.png)

![使用idea的结束rebase](http://47.101.155.205/image-20240819100354403.png)

![image-20240819100609708](http://47.101.155.205/image-20240819100609708.png)



## git-back演示

git log --oneline

![image-20240819101923213](http://47.101.155.205/image-20240819101923213.png)

git rebase -i 5a99d96

![image-20240819101945392](http://47.101.155.205/image-20240819101945392.png)

![image-20240819102000896](http://47.101.155.205/image-20240819102000896.png)

![image-20240819102022290](http://47.101.155.205/image-20240819102022290.png)

git commit --amend

![image-20240819110146248](http://47.101.155.205/image-20240819110146248.png)



本地删除文件

git rebase --continue

![image-20240819110437417](http://47.101.155.205/image-20240819110437417.png)

从开始编辑到最后，有8个分支，现在修改的是第1个，

![image-20240819110843879](http://47.101.155.205/image-20240819110843879.png)

git rebase --continue(再执行一次就好了，自动删除第7的commit了)

![image-20240819110547452](http://47.101.155.205/image-20240819110547452.png)

