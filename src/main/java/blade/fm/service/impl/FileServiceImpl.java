package blade.fm.service.impl;

import java.io.File;

import blade.annotation.Component;
import blade.fm.QiniuApi;
import blade.fm.service.FileService;
import blade.kit.FileKit;
import blade.kit.StringKit;
import blade.kit.log.Logger;

@Component
public class FileServiceImpl implements FileService {

	private Logger logger = Logger.getLogger(FileServiceImpl.class);
	
	@Override
	public void delete(final String key) {
		new Thread() {
			@Override
			public void run() {
				QiniuApi.delete(key);
			}
		}.start();
	}

	
	@Override
	public void upload(final String key, final String filePath) {
		if (FileKit.isFile(filePath) && StringKit.isNotBlank(key)) {
			new Thread() {
				public void run() {
					
					QiniuApi.uploadFile(key, new File(filePath));
					
				};
			}.start();
		}
	}
	
}
