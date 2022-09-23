package boardClone.common;

import java.io.File;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import boardClone.dto.BoardFileDto;

@Component
public class FileUtils {

	// ��û�� ���ؼ� ���޹��� ������ �����ϰ�, ���� ������ ��ȯ�ϴ� �޼���
	public List<BoardFileDto> parseFileInfo(int boardIdx, MultipartHttpServletRequest request) throws Exception {
		
		if (ObjectUtils.isEmpty(request)) {
			return null;
		}
		
		List<BoardFileDto> fileInfoList = new ArrayList<>();
		
		// ������ ������ ���͸��� ����
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
		ZonedDateTime now = ZonedDateTime.now();
		String storedDir = "images/" + now.format(dtf);
		File fileDir = new File(storedDir);
		if (!fileDir.exists()) {
			fileDir.mkdir();
		}
		
		// ���ε� ���� �����͸� ���͸��� �����ϰ� ������ ����Ʈ�� ����
		Iterator<String> fileTagNames = request.getFileNames();
		while (fileTagNames.hasNext()) {
			String originalFileExtension = "";
			String fileTagName = fileTagNames.next();
			List<MultipartFile> files = request.getFiles(fileTagName);
			for (MultipartFile file : files) {
				if (!file.isEmpty()) {
					String contentType = file.getContentType();
					if (ObjectUtils.isEmpty(contentType)) {
						break;
					} else {
						if (contentType.contains("image/jpeg")) {
							originalFileExtension = ".jpg";
						} else if (contentType.contains("image/png")) {
							originalFileExtension = ".png";
						} else if (contentType.contains("image/gif")) {
							originalFileExtension = ".gif";
						} else {
							break;
						}
					}
					
					// ���忡 ����� ���� �̸��� ����
					String storedFileName = Long.toString(System.nanoTime()) + originalFileExtension;
					
					// ���� ������ ����Ʈ�� ���� 
					BoardFileDto bfd = new BoardFileDto();
					bfd.setBoardIdx(boardIdx);
					bfd.setFileSize(file.getSize());
					bfd.setOriginalFileName(file.getOriginalFilename());
					bfd.setStoredFilePath(storedDir + "/" + storedFileName);
					fileInfoList.add(bfd);
					
					// ���� ����
					fileDir = new File(storedDir + "/" + storedFileName);
					file.transferTo(fileDir);
				}
			}
		}
		return fileInfoList;	
	}
}
