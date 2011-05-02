%% Benchmark#1.

clc; clear all; close all;
display(['Benchmark #1']);

%% Define search space.
X = 0:0.5:90;   % Theta (degree)
Y = 0.5:0.02:3; % Length (meter)
Z = [];         % Directivity (dimensionless)

%% Compute directivity (NEC).
% (expected time: 36 seconds)
tic
for i = 1:length(Y),
    display(['./CALLNEC.exe ' num2str(i)]);
    system(['./outputs/CALLNEC.exe ' num2str(i)]);
    z = dlmread('GAIN.OUT');
    system(['rm NEC.INP NEC.OUT GAIN.OUT']);
    Z = [Z 10.^(z(1:181,2)/10)];
end
Z = Z';
toc

%% Plot directivity (NEC).
figure('Name', 'Benchmark#1 - (NEC)');

[X, Y] = meshgrid(X, Y);
surf(X, Y, Z, 'EdgeAlpha', 0.3);

xlabel('Theta (degree)', 'FontSize', 14);
ylabel('Length (wavelengths)', 'FontSize', 14);
zlabel('Directivity (dimensionless)', 'FontSize', 14);

%% Save directivity (NEC).
dlmwrite('outputs/directivity-b1.txt', Z');
save('outputs/directivity-b1.m', 'X', 'Y', 'Z');

%% Compute fitted directivity (cubic interpolation).
% opts = fitoptions('cubicinterp');
% opts.Weights = zeros(1,0);
% opts.Normalize = 'on';
% [fitresult, gof] = fit([X(:), Y(:)], Z(:), 'cubicinterp', opts);
% 
% %% Plot fitted directivity (Cubic Interpolation).
% figure('Name', 'Benchmark#1 - (NEC + Cubic Interpolation)');
% 
% plot(fitresult, [X(:), Y(:)], Z(:));
% xlabel('Theta (degree)', 'FontSize', 20);
% ylabel('Length (meter)', 'FontSize', 20);
% zlabel('Directivity (dimensionless)', 'FontSize', 20);
% view(-53.5, 40);
% 
% %% Plot fitted directivity (Cubic Interpolation).
% figure('Name', 'Benchmark#1 - (NEC + Cubic Interpolation)');
% 
% step = 0.01;
% XX = 0:step:90;
% YY = 0.5:step:3;
% 
% [XX, YY] = meshgrid(XX, YY);
% ZZ = fitresult(XX(:), YY(:));
% ZZ = reshape(ZZ, size(XX));
% 
% surf(XX, YY, ZZ, 'EdgeAlpha', 0.3);
% xlabel('Theta (degree)', 'FontSize', 20);
% ylabel('Length (meter)', 'FontSize', 20);
% zlabel('Directivity (dimensionless)', 'FontSize', 20);
% 
% %% Save directivity (Cubic Interpolation).
% dlmwrite('outputs/directivity-b1-ci.txt', ZZ');
% save('outputs/directivity-b1-ci.m', 'XX', 'YY', 'ZZ');
