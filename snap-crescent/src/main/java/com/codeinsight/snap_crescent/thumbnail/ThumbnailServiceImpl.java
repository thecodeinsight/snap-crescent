package com.codeinsight.snap_crescent.thumbnail;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeinsight.snap_crescent.common.utils.Constant;
import com.codeinsight.snap_crescent.common.utils.FileService;
import com.codeinsight.snap_crescent.config.EnvironmentProperties;
import com.codeinsight.snap_crescent.common.utils.Constant.FILE_TYPE;
import com.codeinsight.snap_crescent.photoMetadata.PhotoMetadata;

@Service
public class ThumbnailServiceImpl implements ThumbnailService {

	@Value("${thumbnail.size.width}")
	private int THUMBNAIL_WIDTH;

	@Value("${thumbnail.size.height}")
	private int THUMBNAIL_HEIGHT;

	@Value("${thumbnail.output.nameSuffix}")
	private String THUMBNAIL_OUTPUT_NAME_SUFFIX;
	
	@Autowired
	private FileService fileService;

	@Autowired
	private ThumbnailRepository thumbnailRepository;

	private final String FILE_TYPE_SEPARATOR = ".";

	public Thumbnail generateThumbnail(File file, PhotoMetadata photoMetadata) throws Exception {
		
		File directory = new File(EnvironmentProperties.STORAGE_PATH + Constant.THUMBNAIL_FOLDER);
		if (!directory.exists()) {
			directory.mkdir();
		}

		boolean isThumbnailCreated = createThumbnail(file, photoMetadata);
		if (isThumbnailCreated) {
			Thumbnail thumbnail = new Thumbnail();
			String thumbnailName = getThumbnailName(file);
			thumbnail.setName(thumbnailName);
			thumbnail.setPath(thumbnailName);
			return thumbnail;
		}

		return null;
	}

	private boolean createThumbnail(File file, PhotoMetadata photoMetadata) {
		
		boolean isThumbnailCreated = false;
		try {

			BufferedImage original = ImageIO.read(file);

			// Rotate Image based on EXIF orientation
			AffineTransform transform = getExifTransformation(photoMetadata.getOrientation(), original.getWidth(),
					original.getHeight());
			AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
			original = op.filter(original, null);

			// Crop Image for a square thumbnail
			int side = Math.min(original.getWidth(), original.getHeight());
			int x = (original.getWidth() - side) / 2;
			int y = (original.getHeight() - side) / 2;
			BufferedImage cropped = original.getSubimage(x, y, side, side);

			// Resize Image
			Image scaledImage = cropped.getScaledInstance(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT,
					BufferedImage.SCALE_SMOOTH);
			BufferedImage bufferedImage = new BufferedImage(THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT,
					BufferedImage.TYPE_INT_RGB);
			bufferedImage.createGraphics().drawImage(scaledImage, 0, 0, null);

			// Save Image as generated thumbnail
			File outputFile = new File(EnvironmentProperties.STORAGE_PATH + Constant.THUMBNAIL_FOLDER + getThumbnailName(file));
			ImageIO.write(bufferedImage, photoMetadata.getFileExtension(), outputFile);

			isThumbnailCreated = true;
		} catch (IOException exception) {
			System.out.println("Unable to read image file: " + file.getName());
		}
		
		return isThumbnailCreated;
	}

	private String getThumbnailName(File file) {
		String extension = FilenameUtils.getExtension(file.getName());
		String fileName = FilenameUtils.removeExtension(file.getName());
		return fileName + THUMBNAIL_OUTPUT_NAME_SUFFIX + FILE_TYPE_SEPARATOR + extension;
	}

	@Override
	@Transactional
	public byte[] getById(Long id) {
		Thumbnail thumbnail = thumbnailRepository.findById(id);
		String fileUniqueName = thumbnail.getPath();
		return fileService.readFileBytes(FILE_TYPE.THUMBNAIL,fileUniqueName);
	}

	public static AffineTransform getExifTransformation(int orientation, int width, int height) {

		AffineTransform t = new AffineTransform();

		switch (orientation) {
		case 1:
			break;
		case 2: // Flip X
			t.scale(-1.0, 1.0);
			t.translate(-width, 0);
			break;
		case 3: // PI rotation
			t.translate(width, height);
			t.rotate(Math.PI);
			break;
		case 4: // Flip Y
			t.scale(1.0, -1.0);
			t.translate(0, -height);
			break;
		case 5: // - PI/2 and Flip X
			t.rotate(-Math.PI / 2);
			t.scale(-1.0, 1.0);
			break;
		case 6: // -PI/2 and -width
			t.translate(height, 0);
			t.rotate(Math.PI / 2);
			break;
		case 7: // PI/2 and Flip
			t.scale(-1.0, 1.0);
			t.translate(-height, 0);
			t.translate(0, width);
			t.rotate(3 * Math.PI / 2);
			break;
		case 8: // PI / 2
			t.translate(0, width);
			t.rotate(3 * Math.PI / 2);
			break;
		}

		return t;
	}

}
