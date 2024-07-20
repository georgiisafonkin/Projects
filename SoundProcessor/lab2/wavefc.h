#pragma once

#include <iostream>
#include <fstream>
#include <sstream>
#include <algorithm>
#include <memory>
#include <cstring>
#include <vector>
#include <string>
#include <cctype>

namespace wavefc { //wave files convertions

	const uint8_t RIFF[4] = { 'R', 'I', 'F', 'F' };
	const uint8_t WAVE[4] = {'W', 'A', 'V', 'E'};
	const uint8_t FMT[4] = {'f', 'm', 't', ' '};
	const uint32_t FMT_SIZE = 16;
	const uint16_t AUDIO_FORMAT = 1;
	const uint16_t NUM_CHANNELS = 1;
	const uint32_t SAMPLE_RATE = 44100;
	const uint32_t BYTE_RATE = 88200;
	const uint16_t BLOCK_ALIGN = 2; //кол-во байт для одного сэмпла, включая все каналы
	const uint16_t BITS_PER_SAMPLE = 16;
	const uint8_t DATA[4] = { 'd', 'a', 't', 'a' };
	const uint8_t HEADER_SIZE = 44;

	#pragma pack(push, 1)
	struct wave_header {
		uint8_t riff[4];
		uint32_t riff_size;
		uint8_t wave[4];
		uint8_t fmt[4];
		uint32_t fmt_size;
		uint16_t audio_format;
		uint16_t num_channels;
		uint32_t sample_rate;
		uint32_t byte_rate;
		uint16_t block_align; //кол-во байт для одного сэмпла, включая все каналы
		uint16_t bits_per_sample;
		uint8_t data[4];
		uint32_t data_size;
	};
	#pragma pack(pop)

	class Reader {
	public:
			virtual ~Reader() = default;
	};
	
	class CommandsReader : public Reader {
	private:
		std::vector<std::string> _files;
		std::ifstream _txt_file;
	public:
		//constructors, destructors
		CommandsReader(std::size_t argc, char* argv[]);
		~CommandsReader();
		//methods
		virtual std::vector<std::string> read_command();
		std::vector<std::string> get_files();
	};

	class WaveReader : public Reader {
	private:
		std::fstream _wav_file;
		std::string _line;
		wave_header _head;
		std::size_t _position;
		std::size_t _second;
	public:
		//constructors, destructors
		WaveReader(std::string file_name);
		~WaveReader();
		//methods
		void read_header();
		std::vector<int16_t> read_second();
		wave_header get_header();
		std::fstream& get_stream();
	};

	class Convertor {
	public:
		virtual ~Convertor() = default;
		virtual void convert(std::vector<std::string> command, std::string output_file_name) = 0;
	};

	class Writer {
	private:
		std::fstream _result_wav_file;
		std::size_t _current_position;
	public:
		Writer(std::string out_file);
		~Writer();
		void write_header(wave_header head_to_write);
		void write_string(std::vector<int16_t> str_to_write);
		std::fstream& get_stream();
	};

	class MuteConvertor : public Convertor {
	private:
		int _start_second;
		int _finish_second;
	public:
		MuteConvertor(int sec1, int sec2);
		~MuteConvertor() = default;
		std::vector<int16_t> mute_sec(std::vector<int16_t> converted_second);
		void convert(std::vector<std::string> command, std::string output_file_name) override;
	};

	class MixConvertor : public Convertor {
	private:
		int _start_second;
		int _finish_second;
	public:
		MixConvertor(int sec1, int sec2);
		~MixConvertor() = default;
		std::vector<int16_t> mix_sec(std::vector<int16_t> sec1_str, std::vector<int16_t> sec2_str);
		void convert(std::vector<std::string> command, std::string output_file_name) override;
	};

	//hierarchy of exceptions

	class BaseException : public std::exception {
	protected:
		std::string _error_message;
	};

	class FailFileOpening : public BaseException {
	public:
		FailFileOpening();
		const char* what() const noexcept override;
	};


	class WrongTimeInterval : public BaseException {
	public:
		WrongTimeInterval();
		const char* what() const noexcept override;
	};

	class FailReadingFromFile : public BaseException {
	public:
		FailReadingFromFile();
		const char* what() const noexcept override;
	};

	class FailWritingInFile : public BaseException {
	public:
		FailWritingInFile();
		const char* what() const noexcept override;
	};

	class TooFewArguments : public BaseException {
	public:
		TooFewArguments();
		const char* what() const noexcept override;
	};

	class WrongConvertion : public BaseException {
	public:
		WrongConvertion();
		const char* what() const noexcept override;
	};

	class NoSecond : public BaseException {
	public:
		NoSecond();
		const char* what() const noexcept override;
	};

	class WrongCommandSyntax : public BaseException {
	public:
		WrongCommandSyntax();
		const char* what() const noexcept override;
	};
}
