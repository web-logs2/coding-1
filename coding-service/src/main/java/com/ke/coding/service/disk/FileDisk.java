package com.ke.coding.service.disk;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import javax.annotation.PostConstruct;
import jodd.io.FileUtil;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/9 18:33
 * @description:
 */
@Service
public class FileDisk implements IDisk {

	@SneakyThrows
	@PostConstruct
	void init() {
		boolean existingFile = FileUtil.isExistingFile(new File("filedata.data"));
		if (!existingFile) {
			new File("filedata.data").createNewFile();
		}
	}

	/**
	 * 读取一个指定扇区的数据。
	 *
	 * @param sectorIdx 扇区索引，起始索引为0，终止索引为 {@code sectorCount()-1}
	 * @return 扇区数据，返回的字节数组长度必须等于{@code sectorSize()}
	 */
	@Override
	public byte[] readSector(int sectorIdx) {
		try (RandomAccessFile randomAccessFile = new RandomAccessFile("filedata.data", "r")) {
			byte[] result = new byte[sectorSize()];
			randomAccessFile.seek((long) sectorIdx * sectorSize());
			randomAccessFile.read(result, 0, sectorSize());
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new byte[0];
	}

	/**
	 * 读取几个指定扇区的数据。
	 *
	 * @param sectorIndex 行业指数
	 * @param count       大小
	 * @return {@link byte[]}
	 */
	@SneakyThrows
	@Override
	public byte[] readSector(int sectorIndex, int count) {
		try (RandomAccessFile randomAccessFile = new RandomAccessFile("filedata.data", "r")) {
			byte[] result = new byte[count * sectorSize()];
			randomAccessFile.seek((long) sectorIndex * sectorSize());
			randomAccessFile.read(result, 0, count * sectorSize());
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new byte[0];
	}

	/**
	 * 写一个指定扇区。
	 *
	 * @param sectorIdx  扇区索引，起始索引为0，终止索引为 {@code sectorCount()-1}
	 * @param sectorData 待写入的数据. 长度必须等于{@code sectorSize()}
	 */
	@Override
	public void writeSector(int sectorIdx, byte[] sectorData) {
		try (RandomAccessFile randomAccessFile = new RandomAccessFile("filedata.data", "rw")) {
			randomAccessFile.seek((long) sectorIdx * sectorSize());
			randomAccessFile.write(sectorData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 写一个指定扇区。
	 *
	 * @param sectorIdx  扇区索引，起始索引为0，终止索引为 {@code sectorCount()-1}
	 * @param sectorData 待写入的数据. 长度必须等于{@code sectorSize()}
	 * @param beginIndex beginIndex
	 */
	@Override
	public void appendWriteSector(int sectorIdx, byte[] sectorData, int beginIndex) {
		try (RandomAccessFile randomAccessFile = new RandomAccessFile("filedata.data", "rw");) {
			randomAccessFile.seek((long) sectorIdx * sectorSize() + beginIndex);
			randomAccessFile.write(sectorData);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void format() {
		try (FileWriter fileWriter = new FileWriter("filedata.data");) {
			fileWriter.write("");
			fileWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
