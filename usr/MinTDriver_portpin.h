/* 
 * File:   SnSDriver_portpin.h
 * Author: gksdudxkr@gmail.com   
 *
 */
#ifndef MINTDRIVER_PORTPIN_H
#define MINTDRIVER_PORTPIN_H

#include <stdio.h>
#include <stddef.h>
#include <stdlib.h>
#include <termios.h>
#include <string.h>
#include <signal.h>
#include <errno.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <sys/ioctl.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <unistd.h>
#include <sched.h>
#include <linux/i2c-dev.h>
#include "/MinT/usr/BBBio/BBBiolib.h"

#define BONEPATH    "/sys/devices/bone_capemgr.9/slots"

/* ------------------------------------------------------------ */
#define SENSOR_BUFFER_SIZE      100
#define SENSOR_SAMPLE_RATE      100
#define FETCH_SIZE              10
/* ----------------------------------------------------------- */

unsigned int *buffer[7];
int uartFileDescriptor[5];
int i2cFileDescriptor[2];

//direction IN 0
//direction OUT 1
void setGPIO(int port, int pin, int direction) {
    iolib_setdir(port, pin, direction);
}

void setGPIOHigh(int port, int pin){
    pin_high(port, pin);
}

void setGPIOLow(int port, int pin){
    pin_low(port, pin);
}

int isGPIOHigh(int port, int pin){
    return is_high(port, pin);
}

int isGPIOLow(int port, int pin){
    return is_low(port, pin);
}

void setADC(int adcNumber) {
    const int clk_div = 1;
    const int open_dly = 0;
    const int sample_dly = 1;

    buffer[adcNumber] = (unsigned int*) malloc(sizeof (unsigned int)
            * SENSOR_BUFFER_SIZE);

    BBBIO_ADCTSC_module_ctrl(BBBIO_ADC_WORK_MODE_BUSY_POLLING, clk_div);
    BBBIO_ADCTSC_channel_ctrl(adcNumber, BBBIO_ADC_STEP_MODE_SW_CONTINUOUS,
            open_dly, sample_dly, BBBIO_ADC_STEP_AVG_1, buffer[adcNumber],
            SENSOR_BUFFER_SIZE);

    BBBIO_ADCTSC_channel_enable(adcNumber);
}

float getADCData(int adcNumber) {
    int i;
    int sum = 0;

    BBBIO_ADCTSC_work(FETCH_SIZE);
    for (i = 0; i < FETCH_SIZE; i++) {
        sum += buffer[adcNumber][i];
    }

    return (float) sum / FETCH_SIZE / 4095.0f * 1.8f;
}

void freeADC(int adcNumber) {
    free(buffer[adcNumber]);
}

void setUART(int uartNumber) {
    FILE *uart;
    struct termios uart1, old;
    char systemCommand[50] = "echo BB-UART";
    char overlayPath[40] = " > /sys/devices/bone_capemgr.9/slots";
    char systemCommand2[25] = "stty -F ";
    char uartName[15] = "/dev/ttyO";
    char uartNum[2];

    sprintf(uartNum, "%d", uartNumber);
    strcat(systemCommand, uartNum);
    strcat(systemCommand, overlayPath);
    system(systemCommand);

    strcat(uartName, uartNum);
    strcat(systemCommand2, uartName);
    system(systemCommand2);

    uart = fopen(BONEPATH, "w");
    if (uart == NULL) printf("slots didn't open\n");
    fseek(uart, 0, SEEK_SET);

    fflush(uart);
    fclose(uart);

    //open uart1 for tx/rx
    uartFileDescriptor[uartNumber] = open(uartName, O_RDWR | O_NOCTTY);
    if (uartFileDescriptor[uartNumber] < 0) printf("port failed to open\n");

    memset(&uart1, 0, sizeof (uart));
    uart1.c_iflag = IGNPAR;
    uart1.c_oflag = 0;

    uart1.c_cflag = CS8 | CLOCAL | CREAD;

    uart1.c_cflag |= B9600;
    uart1.c_lflag = 0;
    uart1.c_cc[VTIME] = 0;
    uart1.c_cc[VMIN] = 0;

    tcflush(uartFileDescriptor[uartNumber], TCIFLUSH);
    tcsetattr(uartFileDescriptor[uartNumber], TCSANOW, &uart1);
}

void writeUART(int uartNumber, const char (*data)) {
    if (write(uartFileDescriptor[uartNumber], data, strlen(data)) != strlen(data)) {
        printf("FAIL TO WRITE(UART)\n");
    } else {
        usleep(100000);
        printf("WRITE : %s\n", data);
    }
}

int readUARTString(int uartNumber, char (*data)) {
    char temp[255];
    int rdcnt;

    rdcnt = read(uartFileDescriptor[uartNumber], temp, sizeof (temp));
    usleep(100000);
    if (rdcnt > 0) {
        temp[rdcnt] = 0;
        strcpy(data, temp);
        printf("READ : %s\n", data);
    }

    return rdcnt;
}

int readUARTBytes(int uartNumber, char (*data), int length) {
    int i;
    int rdcnt;

    rdcnt = read(uartFileDescriptor[uartNumber], data, length);
    usleep(100000);
    for (i = 0; i < length; i++)
        printf("%x "); //Hex print
    printf("\n");

    return rdcnt;
}

void setI2C(int i2cNumber, int address) {
    char i2cFile[11] = "/dev/i2c-";
    char systemCommand[50] = "echo BB-I2C";
    char overlayPath[40] = " > /sys/devices/bone_capemgr.9/slots";
    char i2cNum[2];

    sprintf(i2cNum, "%d", i2cNumber);
    strcat(systemCommand, i2cNum);
    strcat(systemCommand, overlayPath);
    system(systemCommand);
    system(systemCommand);

    strcat(i2cFile, i2cNum);
    if ((i2cFileDescriptor[i2cNumber] = open(i2cFile, O_RDWR)) < 0) {
        perror("Failed to open the I2C\n");
        exit(1);
    }
    if (ioctl(i2cFileDescriptor[i2cNumber], I2C_SLAVE, address) < 0) {
        printf("Failed to acquire bus access\n");
        exit(1);
    }

}

void writeI2C(int i2cNumber, char address, char data) {
    char buf[2] = {address, data};

    if (write(i2cFileDescriptor[i2cNumber], buf, 2) != 2) {
        printf("FAIL TO WRITE(I2C)\n");
    }
    //else
    //{
    //	printf("WRITE : %c\n", data);
    //}
}

char readI2C(int i2cNumber, char address) {
    char respond;

    if (write(i2cFileDescriptor[i2cNumber], &address, 1) != 1) {
        printf("FAIL TO WRITE ADDRESS(I2C)\n");
    } else {
        if (read(i2cFileDescriptor[i2cNumber], &respond, 1) != 1)
            printf("FAIL TO READ(I2C)\n");
        //else
        //	printf("READ : %c\n", respond);
    }

    return respond;
}

#endif //MINTDRIVER_PORTPIN_H
