#include "wavefc.h"

using namespace wavefc;

CommandsReader::CommandsReader(std::size_t argc, char* argv[]) {
	_txt_file.open(argv[1]);
	if (!_txt_file.is_open()) {
		throw FailFileOpening();
	}

	std::size_t i = 2;
	while (i < argc) {
		_files.push_back(argv[i]);
		i++;
	}
}

CommandsReader::~CommandsReader() { _txt_file.close(); }

std::vector<std::string> CommandsReader::read_command() {
	std::vector<std::string> command;
	std::string line;
	std::string word;

	if (!getline(_txt_file, line)) {
		command.push_back("None");
		return command;
	}

	std::stringstream stream(line);

	while (getline(stream, word, ' ')) {
		command.push_back(word);
	}

	int i = 1;
	while (command.at(i)[0] == '$') {
		std::string checked_str = command.at(i).substr(1);
		for (int j = 0; j < checked_str.size(); j++) {
			if (!std::isdigit(checked_str.at(j))) {
				throw WrongCommandSyntax();
			}
		}
		std::string tmp = command.at(i).substr(1);
		std::size_t n = std::stoul(tmp);
		command.at(i) = _files[n];
		i++;
	}

	if (command.at(0) == "mix" && command.size() <= 4) {
		throw NoSecond();
	}

	if (command.at(0) == "mute" && command.size() < 4) {
		throw NoSecond();
	}

	return command;
}

std::vector<std::string> CommandsReader::get_files() {
	return _files;
}
