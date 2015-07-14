package blade.fm.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import blade.annotation.Component;
import blade.fm.model.Mcat;
import blade.fm.service.McatService;

@Component
public class McatServiceImpl implements McatService {

	private Mcat model = new Mcat();
	
	@Override
	public Mcat get(Integer id) {
		return model.select().fetchByPk(id);
	}
	
	@Override
	public List<Mcat> getList(Integer status) {
		return model.select().where("status", status).fetchList();
	}

	@Override
	public boolean save(String name) {
		if (StringUtils.isNotBlank(name)) {
			return model.insert().param("name", name).param("status", 1).executeAndCommit(Integer.class) > 0;
		}
		return false;
	}

	@Override
	public boolean update(Integer id, String name, Integer status) {
		return model.update().param("name", name).param("status", status)
				.where("id", id).executeAndCommit(Integer.class) > 0;
		
	}
}
