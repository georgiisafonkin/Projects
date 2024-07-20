#include <iostream>
#include "wavefc.h"

int main(int argc, char* argv[]) {
	try
	{
		if (argc < 4) {
			if (std::strcmp(argv[1], "-h") == 0) {
				std::cout << "sound_processor.exe the_name_of_commands_file.txt the_name_of_output_file.wav wav_files_to_convert.wav ..." << std::endl;
				return 0;
			}
			throw wavefc::TooFewArguments();
		}
		wavefc::CommandsReader cmdreader(argc, argv);
		std::string output_file_name = cmdreader.get_files().at(0);
		std::ofstream ofs;
		ofs.open(output_file_name, std::ios::out);
		if (!ofs.is_open()) {
			throw wavefc::FailFileOpening();
		}
		ofs.close();
		for (;;) {
			std::vector<std::string> cmd = cmdreader.read_command();
			if (cmd.at(0) == "None") {
				break;
			}
			if (cmd.at(0) == "mute") {
				int sec1 = std::stoi(cmd.at(2));
				int sec2 = std::stoi(cmd.at(3));
				if (sec2 < sec1) {
					throw wavefc::WrongTimeInterval();
				}
				std::unique_ptr<wavefc::Convertor> mute_convertor = std::make_unique<wavefc::MuteConvertor>(sec1, sec2);
				mute_convertor->convert(cmd, output_file_name);
			}
			else if (cmd.at(0) == "mix") {
				int sec1 = std::stoi(cmd.at(3));
				int sec2 = std::stoi(cmd.at(4));
				if (sec2 < sec1) {
					throw wavefc::WrongTimeInterval();;
				}
				std::unique_ptr<wavefc::Convertor> mix_convertor = std::make_unique<wavefc::MixConvertor>(sec1, sec2);
				mix_convertor->convert(cmd, output_file_name);
			}
			else {
				throw wavefc::WrongConvertion();
			}
		}
	}
	catch (wavefc::FailFileOpening err)
	{
		std::cerr << err.what() << std::endl;
		return 0;
	}
	catch (wavefc::FailReadingFromFile err)
	{
		std::cerr << err.what() << std::endl;
		return 0;
	}
	catch (wavefc::FailWritingInFile err)
	{
		std::cerr << err.what() << std::endl;
		return 0;
	}
	catch (wavefc::WrongTimeInterval err)
	{
		std::cerr << err.what() << std::endl;
		return 0;
	}
	catch (wavefc::TooFewArguments err)
	{
		std::cerr << err.what() << std::endl;
		return 0;
	}
	catch (wavefc::WrongConvertion err)
	{
		std::cerr << err.what() << std::endl;
		return 0;
	}
	catch (wavefc::NoSecond err)
	{
		std::cerr << err.what() << std::endl;
		return 0;
	}
	catch (wavefc::WrongCommandSyntax err)
	{
		std::cerr << err.what() << std::endl;
		return 0;
	}

	return 0;
}
