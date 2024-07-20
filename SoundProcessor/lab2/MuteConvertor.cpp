#include "wavefc.h"

using namespace wavefc;

MuteConvertor::MuteConvertor(int sec1, int sec2) : _start_second{ sec1 }, _finish_second{ sec2 } {}

std::vector<int16_t> MuteConvertor::mute_sec(std::vector<int16_t> converted_second) {
	std::fill(converted_second.data(), converted_second.data() + converted_second.size(), '0');
	return converted_second;
}

void MuteConvertor::convert(std::vector<std::string> command, std::string output_file_name) {
	wavefc::WaveReader reader(command.at(1));
	reader.read_header();
	wavefc::Writer writer(output_file_name);
	writer.write_header(reader.get_header());
	while ((writer.get_stream().tellp() < wavefc::HEADER_SIZE + _start_second * wavefc::BYTE_RATE)) {
		writer.write_string(reader.read_second());
	}
	for (std::size_t i = 0; i < _finish_second - _start_second; i++) {
		writer.write_string(this->mute_sec(reader.read_second()));
	}
	while (!reader.get_stream().eof()) {
		writer.write_string(reader.read_second());
	}
}
