# SFTP Client Testing System

This project is a testing system designed to validate the functionality of an SFTP client. The system includes unit tests, integration tests, and automated scripts to ensure the SFTP client works as expected. The tests cover both positive and negative scenarios, including connection handling, file operations, and domain-IP pair management.

## Features

- **Unit Tests**: Validate individual methods of the SFTP client, such as parsing JSON, formatting JSON, and managing domain-IP pairs.
- **Integration Tests**: Test the SFTP client's interaction with an SFTP server, including file upload/download and connection handling.
- **Negative Tests**: Ensure the client handles invalid inputs and edge cases gracefully.
- **Automated Scripts**: Use Expect scripts to simulate user interactions and automate testing workflows.
- **TestNG Framework**: Leverage TestNG for test organization, execution, and reporting.

## Prerequisites

- Java SE 8 or higher.
- Maven for building the project.
- An SFTP server for integration testing.
- Expect (for running automated scripts, optional).

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/sftp-client-testing-system.git
   cd sftp-client-testing-system
