clc; clear all; close all;
display(['Benchmark #1']);

% Define search space.
X = 0:0.5:90;   % Theta (degree)
Y = 0.5:0.02:3; % Length (meter)
Z = [];         % Directivity (dimensionless)

% Compute directivity.
% (expected time: 36 seconds)
tic
for i = 1:length(Y),
    display(['./CALLNEC.exe ' num2str(i)]);
    system(['./CALLNEC.exe ' num2str(i)]);
    z = dlmread('GAIN.OUT');
    Z = [Z 10.^(z(1:181,2)/10)];
end
toc

% Display results.
[X, Y] = meshgrid(X, Y);
surf(X, Y, Z', 'EdgeAlpha', 0.3);
xlabel('Theta (degree)', 'FontSize', 20);
ylabel('Length (meter)', 'FontSize', 20);
zlabel('Directivity (dimensionless)', 'FontSize', 20);

% Save results to disk.
dlmwrite('directivity-b1.txt', Z);