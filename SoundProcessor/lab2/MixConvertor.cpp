#include "wavefc.h"

using namespace wavefc;

MixConvertor::MixConvertor(int sec1, int sec2) : _start_second{ sec1 }, _finish_second{ sec2 } {}

std::vector<int16_t> MixConvertor::mix_sec(std::vector<int16_t> sec1_str, std::vector<int16_t> sec2_str) {
	std::vector<int16_t> mixed_sec;
	mixed_sec.reserve(SAMPLE_RATE);
	for (std::size_t i = 0; i < SAMPLE_RATE; i++) {
		int16_t el1 = sec1_str[i];
		int16_t el2 = sec2_str[i];
		int16_t el = (el1 + el2) / 2;
		mixed_sec.push_back(el);
	}
	return mixed_sec;
}

void MixConvertor::convert(std::vector<std::string> command, std::string output_file_name) {
	wavefc::WaveReader reader1(command.at(1));
	reader1.read_header();
	wavefc::WaveReader reader2(command.at(2));
	reader2.read_header();
	wavefc::Writer writer(output_file_name);
	writer.write_header(reader1.get_header());
	while ((writer.get_stream().tellp() < wavefc::HEADER_SIZE + _start_second * wavefc::BYTE_RATE)) {
		writer.write_string(reader1.read_second());
		reader2.read_second();
	}
	for (std::size_t i = 0; i < _finish_second - _start_second; i++) {
		writer.write_string(this->mix_sec(reader1.read_second(), reader2.read_second()));
	}
	while (!reader1.get_stream().eof()) {
		writer.write_string(reader1.read_second());
	}
}
