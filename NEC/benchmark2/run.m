clc; clear all; close all;
display(['Benchmark #2']);

% Define search space.
X = 0:0.5:180;              % Theta (degree)
Y = 5 + ((1:201)-1)*0.05;   % Length (meter)
Z = [];                     % Directivity (dimensionless)

% Compute directivity.
% (expected time: 441 seconds)
tic
for y = Y,
    display(['./CALLNEC.exe ' num2str(y)]);
    system(['./CALLNEC.exe ' num2str(y)]);
    z = dlmread('GAIN.OUT');
    Z = [Z 10.^(z(:,2)/10)];
end
toc

% Display results.
[X, Y] = meshgrid(X, Y);
surf(X, Y, Z' + normrnd(0,0.1, 201, 361), 'EdgeAlpha',0.3);
xlabel('Theta (degree)', 'FontSize', 20);
ylabel('Distance (meter)', 'FontSize', 20);
zlabel('Directivity (dimensionless)', 'FontSize', 20);

% Save results to disk.
dlmwrite('directivity-b2.txt', Z);