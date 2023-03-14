package com.ke.coding.service.disk;

import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * @author: xueyunlong001@ke.com
 * @time: 2023/3/9 18:33
 * @description:
 */
@Service
public class FileDisk implements IDisk {

	/**
	 * 读取一个指定扇区的数据。
	 *
	 * @param sectorIdx 扇区索引，起始索引为0，终止索引为 {@code sectorCount()-1}
	 * @return 扇区数据，返回的字节数组长度必须等于{@code sectorSize()}
	 */
	@Override
	public byte[] readSector(int sectorIdx) {
		Resource resource = new ClassPathResource("filedata.data");
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(resource.getFile(), "r")) {
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
	@Override
	public byte[] readSector(int sectorIndex, int count) {
		Resource resource = new ClassPathResource("filedata.data");
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(resource.getFile(), "r")) {
			byte[] result = new byte[count * sectorSize()];
			randomAccessFile.seek((long) sectorIndex * sectorSize());
			randomAccessFile.read(result, 0, count * sectorSize());
			return result;
		} catch (IOException e) {
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
		Resource resource = new ClassPathResource("filedata.data");
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(resource.getFile(), "rw");) {
			randomAccessFile.seek((long) sectorIdx * sectorSize());
			randomAccessFile.write(sectorData);
		} catch (IOException e) {
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
		Resource resource = new ClassPathResource("filedata.data");
		try (RandomAccessFile randomAccessFile = new RandomAccessFile(resource.getFile(), "rw");) {
			randomAccessFile.seek((long) sectorIdx * sectorSize());
			randomAccessFile.seek(beginIndex);
			randomAccessFile.write(sectorData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void format() {
		Resource resource = new ClassPathResource("filedata.data");
		try (FileWriter fileWriter =new FileWriter(resource.getFile());) {
			fileWriter.write("");
			fileWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
