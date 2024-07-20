#include "wavefc.h"

using namespace wavefc;

FailFileOpening::FailFileOpening() {
	_error_message = "Error. Can't open the file";
}

const char* FailFileOpening::what() const noexcept {
	return _error_message.c_str();
}

WrongTimeInterval::WrongTimeInterval() {
	_error_message = "Error. Wrong time interval. Start second > Finish second.";
}

const char* WrongTimeInterval::what() const noexcept {
	return _error_message.c_str();
}

FailReadingFromFile::FailReadingFromFile() {
	_error_message = "Error. Can't read from this file.";
}

const char* FailReadingFromFile::what() const noexcept {
	return _error_message.c_str();
}

FailWritingInFile::FailWritingInFile() {
	_error_message = "Error. Can't write in this file.";
}

const char* FailWritingInFile::what() const noexcept {
	return _error_message.c_str();
}

TooFewArguments::TooFewArguments() {
	_error_message = "Error. Too few arguments. Type -h to open the program guide.";
}

const char* TooFewArguments::what() const noexcept {
	return _error_message.c_str();
}

WrongConvertion::WrongConvertion() {
	_error_message = "Error. The entered converter doesn't exist.";
}

const char* WrongConvertion::what() const noexcept {
	return _error_message.c_str();
}

NoSecond::NoSecond() {
	_error_message = "Error. No start or finish second.";
}

const char* NoSecond::what() const noexcept {
	return _error_message.c_str();
}

WrongCommandSyntax::WrongCommandSyntax() {
	_error_message = "Error. Wrong syntax.";
}

const char* WrongCommandSyntax::what() const noexcept {
	return _error_message.c_str();
}
