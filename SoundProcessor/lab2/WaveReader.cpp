#include "wavefc.h"

using namespace wavefc;

WaveReader::WaveReader(std::string file_name) : _position{ 0 } {
	_wav_file.open(file_name, std::ios::binary | std::ios::in | std::ios::out);
	if (!(_wav_file.is_open())) {
		throw wavefc::FailFileOpening();
	}
}

WaveReader::~WaveReader() { _wav_file.close(); }

void WaveReader::read_header() {
	if (!_wav_file.read(reinterpret_cast<char*>(&_head), sizeof(_head))) {
		throw FailReadingFromFile();
	}
	for (;;) {
		if (_head.data[0] == 'd') {
			break;
		}
		_head.riff_size -= _head.data_size;
		_wav_file.seekg(_head.data_size, std::ios::cur);
		if (!_wav_file.read(reinterpret_cast<char*>(_head.data), sizeof(_head.data))) {
			throw FailReadingFromFile();
		}
		if (!_wav_file.read(reinterpret_cast<char*>(&_head.data_size), sizeof(_head.data_size))) {
			throw FailReadingFromFile();
		}
	}
	_position = _wav_file.tellg();
}

std::vector<int16_t> WaveReader::read_second() {
	std::vector<int16_t> sec;
	sec.reserve(SAMPLE_RATE); //использовать resize(), чтобы менять размер и занулять, тогда смогу по at() обращаться
	sec.resize(SAMPLE_RATE, 0);
	if (!_wav_file.eof()) {
		_wav_file.seekg(_position, std::ios::beg);
		if (!_wav_file.read(reinterpret_cast<char*>(sec.data()), BYTE_RATE) && !_wav_file.eof()) {
			throw FailReadingFromFile();
		}
		_second++;
		_position = _wav_file.tellg();
		return sec;
	}
	return sec;
}

wave_header WaveReader::get_header() {
	return _head;
}

std::fstream& WaveReader::get_stream() {
	return _wav_file;
}
