#include "wavefc.h"

using namespace wavefc;

Writer::Writer(std::string out_file) : _current_position{ 0 } { _result_wav_file.open(out_file, std::ios::binary | std::ios::in | std::ios::out); }

Writer::~Writer() {
	_result_wav_file.close();
}

void Writer::write_header(wave_header head_to_write) {
	if (!_result_wav_file.write(reinterpret_cast<char*>(&head_to_write), sizeof(head_to_write))) {
		throw FailWritingInFile();
	}
	_current_position = _result_wav_file.tellp();
}

void Writer::write_string(std::vector<int16_t> str_to_write) {
	if (!_result_wav_file.write(reinterpret_cast<char*>(str_to_write.data()), BYTE_RATE)) {
		throw FailWritingInFile();
	}
	_current_position = _result_wav_file.tellp();
}

std::fstream& Writer::get_stream() {
	return _result_wav_file;
}
