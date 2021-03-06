package com.codeinsight.snap_crescent.photo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.codeinsight.snap_crescent.appConfig.AppConfigService;
import com.codeinsight.snap_crescent.common.beans.BaseResponseBean;
import com.codeinsight.snap_crescent.common.services.BaseService;
import com.codeinsight.snap_crescent.common.utils.AppConfigKeys;
import com.codeinsight.snap_crescent.common.utils.Constant;
import com.codeinsight.snap_crescent.common.utils.Constant.FILE_TYPE;
import com.codeinsight.snap_crescent.common.utils.Constant.ResultType;
import com.codeinsight.snap_crescent.common.utils.FileService;
import com.codeinsight.snap_crescent.config.EnvironmentProperties;
import com.codeinsight.snap_crescent.photoMetadata.PhotoMetadata;
import com.codeinsight.snap_crescent.photoMetadata.PhotoMetadataRepository;
import com.codeinsight.snap_crescent.photoMetadata.PhotoMetadataService;
import com.codeinsight.snap_crescent.thumbnail.Thumbnail;
import com.codeinsight.snap_crescent.thumbnail.ThumbnailRepository;
import com.codeinsight.snap_crescent.thumbnail.ThumbnailService;

@Service
public class PhotoServiceImpl extends BaseService implements PhotoService {

	@Autowired
	private PhotoMetadataService photoMetadataService;

	@Autowired
	private ThumbnailService thumbnailService;

	@Autowired
	private PhotoRepository photoRepository;

	@Autowired
	private PhotoMetadataRepository photoMetadataRepository;

	@Autowired
	private ThumbnailRepository thumbnailRepository;

	@Autowired
	private AppConfigService appConfigService;

	@Autowired
	private FileService fileService;

	@Autowired
	private PhotoConverter photoConverter;

	@Transactional
	public BaseResponseBean<Long, UiPhoto> search(PhotoSearchCriteria searchCriteria) {

		BaseResponseBean<Long, UiPhoto> response = new BaseResponseBean<>();

		int count = photoRepository.count(searchCriteria);

		if (count > 0) {

			List<UiPhoto> searchResult = photoConverter.getBeansFromEntities(
					photoRepository.search(searchCriteria, searchCriteria.getResultType() == ResultType.OPTION),
					searchCriteria.getResultType());

			response.setTotalResultsCount(count);
			response.setResultCountPerPage(searchResult.size());
			response.setCurrentPageIndex(searchCriteria.getPageNumber());

			response.setObjects(searchResult);

		}

		return response;
	}

	@Override
	@Transactional
	public void upload(ArrayList<MultipartFile> multipartFiles) throws Exception {

		String x = appConfigService.getValue(AppConfigKeys.APP_CONFIG_KEY_SKIP_UPLOADING);
		if (x != null & Boolean.parseBoolean(x) == true) {
			return;
		}
		
		
		File directory = new File(EnvironmentProperties.STORAGE_PATH + Constant.PHOTO_FOLDER);
		if (!directory.exists()) {
			directory.mkdir();
		}
		for (MultipartFile multipartFile : multipartFiles) {
			
			String originalFilename = multipartFile.getOriginalFilename();
			String extension =  originalFilename.substring(originalFilename.lastIndexOf("."));
			
			String path = EnvironmentProperties.STORAGE_PATH + Constant.PHOTO_FOLDER + UUID.randomUUID().toString() + extension;
			
			multipartFile.transferTo(new File(path));

			File file = new File(path);
			if (isAlreadyExist(file)) {
				continue;
			}
			Photo image = new Photo();

			PhotoMetadata photoMetadata = photoMetadataService.extractMetaData(originalFilename, file);
			Thumbnail thumbnail = thumbnailService.generateThumbnail(file, photoMetadata);

			photoMetadataRepository.save(photoMetadata);
			thumbnailRepository.save(thumbnail);

			image.setPhotoMetadataId(photoMetadata.getId());
			image.setThumbnailId(thumbnail.getId());

			photoRepository.save(image);

		}

	}

	private boolean isAlreadyExist(File file) throws Exception {
		boolean exist = false;
		String fileName = file.getName();
		exist = photoMetadataRepository.existsByName(fileName);
		return exist;
	}
	
	@Override
	public UiPhoto getById(Long id) {
		return photoConverter.getBeanFromEntity(photoRepository.findById(id), ResultType.FULL) ;
	}


	@Override
	@Transactional
	public byte[] getImageById(Long id) throws Exception {
		Photo photo = photoRepository.findById(id);
		String fileUniqueName = photo.getPhotoMetadata().getPath();
		return fileService.readFileBytes(FILE_TYPE.PHOTO, fileUniqueName);
	}

	@Override
	@Transactional
	public void update(UiPhoto enity) throws Exception {
		// TODO Auto-generated method stub
		
	}

	
}
