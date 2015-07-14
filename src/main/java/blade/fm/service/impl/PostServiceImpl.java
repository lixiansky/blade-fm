package blade.fm.service.impl;

import java.util.Date;
import java.util.List;

import blade.annotation.Component;
import blade.fm.model.Post;
import blade.fm.service.PostService;
import blade.kit.DateKit;
import blade.kit.log.Logger;
import blade.plugin.sql2o.Page;

@Component
public class PostServiceImpl implements PostService {

	private Logger logger = Logger.getLogger(PostServiceImpl.class);
	
	private Post model = new Post();
	
	@Override
	public Post getByPid(Integer pid) {
		return model.select().where("pid", pid).fetchOne();
	}

	@Override
	public List<Post> getList(Integer uid, String title, String tag, Integer is_pub, String order) {
		return model.select().where("uid", uid).where("title", title)
				.in("tags", tag).where("is_pub", is_pub).orderBy(order).fetchList();
	}

	@Override
	public Page<Post> getPageList(Integer uid, String title, String tag, Integer is_pub, Integer page,
			Integer pageSize, String order) {
		return model.select().where("uid", uid).where("title", title)
		.in("tags", tag).where("is_pub", is_pub).orderBy(order).fetchPage(page, pageSize);
	}

	@Override
	public int delete(Integer pid) {
		int count = 0;
		if (null != pid) {
			try {
				count = model.delete().where("pid", pid).executeAndCommit(Integer.class);
			} catch (Exception e) {
				logger.warn("删除文章失败：" + e.getMessage());
				count = 0;
			}
		}
		return count;
	}

	@Override
	public int delete(String pids) {
		int count = 0;
		if (null != pids) {
			try {
				count = model.delete().in("pid", pids).executeAndCommit(Integer.class);
			} catch (Exception e) {
				logger.warn("删除文章列表失败：" + e.getMessage());
				count = 0;
			}
		}
		return count;
	}

	@Override
	public int update(Integer pid, String title, String content, String tags, Integer allow_comment, Integer is_pub) {
		int count = 0;
		if (null != pid) {
			try {
				count = model.update()
						.param("title", title)
						.param("content", content)
						.param("tags", tags)
						.param("allow_comment", allow_comment)
						.param("is_pub", is_pub)
						.where("pid", pid).executeAndCommit();
			} catch (Exception e) {
				logger.warn("更新文章失败：" + e.getMessage());
				count = 0;
			}
		}
		return count;
	}

	@Override
	public int save(Integer uid, String title, String content, String tags, Integer allow_comment, Integer is_pub) {
		int count = 0;
		Integer currentTime = DateKit.getUnixTimeByDate(new Date());
		try {
			
			count = model.insert()
					.param("uid", uid)
					.param("title", title)
					.param("content", content)
					.param("tags", tags)
					.param("allow_comment", allow_comment)
					.param("is_pub", is_pub)
					.param("post_time", currentTime)
					.param("last_time", currentTime).executeAndCommit();
		} catch (Exception e) {
			logger.warn("保存文章失败：" + e.getMessage());
			count = 0;
		}
		return count;
	}

	@Override
	public int updateHit(Integer pid) {
		if(null != pid){
			Integer last_time = DateKit.getUnixTimeByDate(new Date());
			return model.update().param("hit", "(hit + 1)").param("last_time", last_time).where("pid", pid).executeAndCommit();
		}
		return 0;
	}

}
