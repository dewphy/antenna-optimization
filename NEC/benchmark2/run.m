%% Benchmark#2.

clc; clear all; close all;
display(['Benchmark #2']);

%% Define search space.
X = 0:0.5:180;              % Theta (degree)
Y = 5 + ((1:201)-1)*0.05;   % Length (meter)
Z = [];                     % Directivity (dimensionless)

%% Compute directivity.
% (expected time: 441 seconds)
tic
for y = Y,
    display(['./CALLNEC.exe ' num2str(y)]);
    system(['./outputs/CALLNEC.exe ' num2str(y)]);
    z = dlmread('GAIN.OUT');
    system(['rm NEC.INP NEC.OUT GAIN.OUT']);
    Z = [Z 10.^(z(:,2)/10)];
end
Z = Z';
toc

%% Plot directivity with gaussian noise (mean=0,variance=2).
figure('Name', 'Benchmark#2');

[X, Y] = meshgrid(X, Y);
surf(X, Y, Z + normrnd(0,sqrt(0.2), 201, 361), 'EdgeAlpha',0.2);
xlabel('Theta (degree)', 'FontSize', 16);
ylabel('Distance (wavelengths)', 'FontSize', 16);
zlabel('Directivity (dimensionless)', 'FontSize', 16);

%% Save directivity.
dlmwrite('./outputs/directivity-b2.txt', Z);
save('./outputs/directivity-b2.m', 'X', 'Y', 'Z');

% %% Compute fitted directivity (cubic interpolation).
% opts = fitoptions('cubicinterp');
% opts.Weights = zeros(1,0);
% opts.Normalize = 'on';
% [fitresult, gof] = fit([X(:), Y(:)], Z(:), 'cubicinterp', opts);
% 
% %% Plot fitted directivity (Cubic Interpolation).
% figure('Name', 'Benchmark#2 - without noise (NEC + Cubic Interpolation)');
% 
% plot(fitresult, [X(:), Y(:)], Z(:));
% xlabel('Theta (degree)', 'FontSize', 20);
% ylabel('Distance (meter)', 'FontSize', 20);
% zlabel('Directivity (dimensionless)', 'FontSize', 20);
% 
% %% Plot fitted directivity (Cubic Interpolation).
% figure('Name', 'Benchmark#2 - without noise (NEC + Cubic Interpolation)');
% 
% step = 0.01;
% XX = 0:step:180;
% YY = 5:step:15;
% 
% [XX, YY] = meshgrid(XX, YY);
% ZZ = fitresult(XX(:), YY(:));
% ZZ = reshape(ZZ, size(XX));
% 
% surf(XX, YY, ZZ, 'EdgeAlpha', 0.3);
% xlabel('Theta (degree)', 'FontSize', 20);
% ylabel('Distance (meter)', 'FontSize', 20);
% zlabel('Directivity (dimensionless)', 'FontSize', 20);
% 
% %% Save directivity (Cubic Interpolation).
% ZZ_noise = ZZ + normrnd(0,sqrt(0.2), size(ZZ,1), size(ZZ,2));
% dlmwrite('./outputs/directivity-b2-ci.txt', ZZ_noise');
% save('./outputs/directivity-b2-ci.m', 'XX', 'YY', 'ZZ');
% 
